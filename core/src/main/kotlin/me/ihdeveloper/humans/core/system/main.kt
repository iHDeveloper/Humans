package me.ihdeveloper.humans.core.system

import me.ihdeveloper.humans.core.System
import me.ihdeveloper.humans.core.command.SummonCommand
import me.ihdeveloper.humans.core.corePlugin
import me.ihdeveloper.humans.core.entity.CustomArmorStand
import me.ihdeveloper.humans.core.entity.CustomSkeleton
import me.ihdeveloper.humans.core.entity.Hologram
import me.ihdeveloper.humans.core.entity.PrisonGuard
import me.ihdeveloper.humans.core.overrideEntity
import me.ihdeveloper.humans.core.registerEntity
import net.minecraft.server.v1_8_R3.EntityArmorStand
import net.minecraft.server.v1_8_R3.EntitySkeleton
import org.bukkit.Bukkit
import org.bukkit.event.Cancellable
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.*
import org.bukkit.plugin.java.JavaPlugin

/**
 * A system for registering the entities of the game core
 */
class CustomEntitySystem : System("Core/Custom-Entity"), Listener {

    override fun init(plugin: JavaPlugin) {
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
}

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
