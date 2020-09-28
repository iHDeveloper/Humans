package me.ihdeveloper.humans.core.system

import me.ihdeveloper.humans.core.*
import me.ihdeveloper.humans.core.command.SummonCommand
import me.ihdeveloper.humans.core.entity.CustomArmorStand
import me.ihdeveloper.humans.core.entity.CustomSkeleton
import me.ihdeveloper.humans.core.entity.Hologram
import me.ihdeveloper.humans.core.entity.PrisonGuard
import net.minecraft.server.v1_8_R3.EntityArmorStand
import net.minecraft.server.v1_8_R3.EntitySkeleton
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockEvent
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.InventoryMoveItemEvent
import org.bukkit.event.player.*
import org.bukkit.event.server.ServerListPingEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import org.bukkit.plugin.java.JavaPlugin

/**
 * A system for registering the entities of the game core
 */
class CustomEntitySystem : System("Core/Custom-Entity"), Listener {

    override fun init(plugin: JavaPlugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin)

        logger.info("Overriding entities...")

        /** Override base entities with custom ones */
        overrideEntity(EntityArmorStand::class, CustomArmorStand::class, logger)
        overrideEntity(EntitySkeleton::class, CustomSkeleton::class, logger)

        logger.info("Registering entities...")

        /** Register custom entities */
        registerEntity(Hologram::class, CustomArmorStand::class, logger)
        registerEntity(PrisonGuard::class, CustomSkeleton::class, logger)
    }

    override fun dispose() {}

    /**
     * Prevent the game from spawning entities naturally
     */
    @EventHandler
    fun onSpawn(event: CreatureSpawnEvent) {
        if (event.spawnReason === CreatureSpawnEvent.SpawnReason.CUSTOM)
            return

        event.isCancelled = true
    }
}

/**
 * A system for registering commands
 */
class CommandSystem : System("Core/Command") {
    private val commands = arrayOf(SummonCommand())

    override fun init(plugin: JavaPlugin) {
        logger.info("Registering command executors...")
        commands.forEach {
            logger.debug("Registering command executor for /${it.name}...")

            when {
                plugin.getCommand(it.name) != null -> plugin.getCommand(it.name).executor = it
                corePlugin!!.getCommand(it.name) != null -> corePlugin!!.getCommand(it.name).executor = it
                else -> logger.error("Failed to register command /${it.name}! It's not found in the plugin.yml!")
            }
        }
    }

    override fun dispose() {}
}

/**
 * A system for managing the block events in the game
 */
class BlockSystem : System("Core/Block"), Listener {

    override fun init(plugin: JavaPlugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin)
    }

    override fun dispose() {}

    /**
     * Cancel any block event.
     * The game doesn't want to be conflicted with vanilla block system
     */
    @EventHandler
    fun onEvent(event: BlockEvent) {
        if (event !is Cancellable)
            return

        event.isCancelled = true
    }
}

/**
 * An item for the game menu
 */
val GAME_MENU = ItemStack(Material.NETHER_STAR, 1).apply {
    itemMeta.apply {
        displayName = "§eGame Menu §7(Right click)"
        lore = arrayListOf(
            "§7View your profile in the game!",
            "§7Track your skills, collections, etc...",
            "",
            "§6Click to open!"
        )
    }
}

/**
 * A system for managing the game menu
 */
class MenuSystem : System("Core/Menu"), Listener {

    override fun init(plugin: JavaPlugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin)
    }

    override fun dispose() {}

    /**
     * Prevent the player from clicking on the menu
     */
    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        if (event.clickedInventory !is PlayerInventory)
            return
        if (event.slot != 8)
            return

        event.isCancelled = true
        open(event.whoClicked as Player)
    }

    /**
     * Prevent the player from moving the item
     */
    @EventHandler
    fun onMoveItem(event: InventoryMoveItemEvent) {
        if (event.item !== GAME_MENU)
            return
        event.isCancelled = true
    }

    /**
     * Put the menu when the player joins the server
     */
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        event.player.inventory.setItem(8, GAME_MENU)
    }

    /**
     * Remove the menu when the player quits the server
     */
    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        event.player.inventory.setItem(8, ITEMSTACK_AIR)
    }

    /**
     * Opens the menu when the player is right/left click the menu
     */
    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        val player = event.player
        val action = event.action

        if (
            (action !== Action.RIGHT_CLICK_BLOCK && action !== Action.RIGHT_CLICK_AIR)
            && (action !== Action.LEFT_CLICK_BLOCK && action !== Action.LEFT_CLICK_AIR)
                )
            return

        if (event.item !== GAME_MENU)
            return

        open(player)
    }

    /**
     * Opens the menu
     */
    private fun open(player: Player) {
        // TODO opens the menu to the player
        player.sendMessage("§cCurrently the game menu is disabled!")
        player.sendMessage("§cIt's under heavy development.")
    }
}

/**
 * A system for managing the player events
 */
class PlayerSystem : System("Core/Player"), Listener {

    override fun init(plugin: JavaPlugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin)
    }

    override fun dispose() {}

    /**
     * Sends a welcome message to the players.
     *
     * Prevent from the broadcasting the join message
     */
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        event.player.run {
            // TODO send a special message for new players!
            sendMessage("§eWelcome back, §7Human§e!")
            sendMessage("")
            sendMessage("")

            for (player in Bukkit.getOnlinePlayers()) {
                if (!player.isOp)
                    continue

                player.sendMessage("§8[§e@§8] §a+ §9${player.displayName}")
            }
        }

        event.joinMessage = null
    }

    /**
     * Prevent broadcasting the quit message
     */
    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        for (player in Bukkit.getOnlinePlayers()) {
            if (!player.isOp)
                continue

            player.sendMessage("§8[§e@§8] §c- §9${player.displayName}")
        }

        event.quitMessage = null
    }

    /**
     * Prevent PVP between players in the game
     */
    @EventHandler
    fun onPvP(event: EntityDamageByEntityEvent) {
        if (event.entityType !== EntityType.PLAYER)
            return
        if (event.damager.type !== EntityType.PLAYER)
            return
        event.isCancelled = true
    }

    /**
     * Prevent the player from picking up items
     */
    @EventHandler
    fun onPickup(event: PlayerPickupItemEvent) {
        event.isCancelled = true
    }

    /**
     * Prevent the player from dropping items
     */
    @EventHandler
    fun onDrop(event: PlayerDropItemEvent) {
        event.isCancelled = true
    }

    /**
     * Prevent the player from getting hunger
     */
    @EventHandler
    fun onFoodLevel(event: FoodLevelChangeEvent) {
        event.foodLevel = 20
    }

}

const val SERVER_MOTD = """
    §e§lHUMANS §8§l- §7§lv0.0-ALPHA
"""

/**
 * A system for handling login and ping events
 */
class LoginSystem : System("Core/Login"), Listener {

    override fun init(plugin: JavaPlugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin)
    }

    override fun dispose() {}

    // TODO Handle login event for loading profiles

    @EventHandler
    fun onPing(event: ServerListPingEvent) {
        event.motd = SERVER_MOTD
    }
}
