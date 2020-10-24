package me.ihdeveloper.humans.core.system

import me.ihdeveloper.humans.core.Configuration
import me.ihdeveloper.humans.core.System
import me.ihdeveloper.humans.core.Warp
import me.ihdeveloper.humans.core.WarpInfo
import me.ihdeveloper.humans.core.entity.WarpCart
import me.ihdeveloper.humans.core.util.GameLogger
import me.ihdeveloper.humans.core.warps
import me.ihdeveloper.humans.core.warpsInfo
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftMinecart
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.vehicle.VehicleExitEvent
import org.bukkit.event.vehicle.VehicleMoveEvent
import org.bukkit.plugin.java.JavaPlugin

/**
 * A system to manage multiple warps
 */
class WarpSystem : System("Core/Warp"), Listener {
    companion object {
        private val config = Configuration("warps")
        private var logger: GameLogger? = null

        fun save() {
            val rawWarps = arrayListOf<Map<String, Any?>>()
            for (info in warpsInfo) {
                rawWarps.add(info.serialize())
            }
            config.set("warps", rawWarps)

            config.save(logger)
        }
    }

    override fun init(plugin: JavaPlugin) {
        Companion.logger = logger
        Bukkit.getPluginManager().registerEvents(this, plugin)
    }

    override fun lateInit(plugin: JavaPlugin) {
        config.load(logger)

        val rawWarps: ArrayList<Map<String, Any?>> = config.get("warps", arrayListOf())
        rawWarps.forEach {
            val info = WarpInfo.deserialize(it)
            val warp = Warp.fromInfo(info)

            warpsInfo.add(info)

            logger.info("Initializing warp with destination ${info.displayName}")
            warp.init(logger)
            warps.add(warp)
        }
    }

    override fun dispose() {
        warps.forEach { 
            it.dispose()
        }
        
        warps.clear()
    }

    /**
     * Check if the player is going through a warp gate
     */
    @EventHandler(priority = EventPriority.LOWEST)
    @Suppress("UNUSED")
    fun onMove(event: PlayerMoveEvent) {
        warps.forEach {
            event.run {
                if (player.vehicle != null && player.vehicle.type === EntityType.MINECART)
                    return

                if (!it.check(player))
                    return

                it.join(player)
            }
        }
    }

    /**
     * Check if the warp cart is about to reach to the end.
     * If so, then teleport it to the start.
     */
    @EventHandler
    fun onVehicleMove(event: VehicleMoveEvent) {
        event.run {
            if (vehicle.type !== EntityType.MINECART)
                return

            if ((vehicle as CraftMinecart).handle !is WarpCart)
                return

            if (vehicle.passenger == null)
                vehicle.remove()

            val cart = (vehicle as CraftMinecart).handle as WarpCart
            if (to.block.location.distance(cart.warp.end.block.location) > 1)
                return

            vehicle.passenger.teleport(cart.warp.spawn)
            vehicle.remove()
        }
    }

    @EventHandler
    fun onExit(event: VehicleExitEvent) {
        if (event.vehicle.type !== EntityType.MINECART || event.exited.type !== EntityType.PLAYER)
            return
        if ((event.vehicle as CraftMinecart).handle !is WarpCart)
            return
        event.isCancelled = true
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        event.run {
            if (player.vehicle == null)
                return

            val vehicle = player.vehicle
            if (vehicle.type !== EntityType.MINECART)
                return

            if ((vehicle as CraftMinecart).handle !is WarpCart)
                return

            vehicle.passenger = null
            vehicle.remove()
        }
    }
}
