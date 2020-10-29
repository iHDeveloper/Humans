package me.ihdeveloper.humans.mine.system

import me.ihdeveloper.humans.core.Configuration
import me.ihdeveloper.humans.core.System
import me.ihdeveloper.humans.core.entity.CustomArmorStand
import me.ihdeveloper.humans.core.registry.registerEntity
import me.ihdeveloper.humans.core.util.GameLogger
import me.ihdeveloper.humans.mine.Mine
import me.ihdeveloper.humans.mine.entity.PrisonMineCrystal
import me.ihdeveloper.humans.mine.entity.PrisonMineWizard
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin

/**
 * A system for handling entities and registering
 */
class EntitySystem : System("Mine/Entity") {

    override fun init(plugin: JavaPlugin) {
        registerEntity(PrisonMineWizard::class, CustomArmorStand::class, logger)
        registerEntity(PrisonMineCrystal::class, CustomArmorStand::class, logger)
    }

    override fun dispose() {}
}

/**
 * A system for managing and handling mines in the server
 */
class MineSystem : System("Mine"), Listener {
    companion object {
        lateinit var plugin: JavaPlugin
        lateinit var logger: GameLogger

        val mines = mutableListOf<Mine>()

        private val config = Configuration("mines")

        fun save() {
            config.set("mines", arrayListOf<Map<String, Any>>().apply {
                mines.forEach { add(it.serialize()) }
            })
            config.save(logger)
        }
    }

    override fun init(plugin: JavaPlugin) {
        Companion.plugin = plugin
        Companion.logger = logger

        config.load(logger)
        val rawMines = config.get("mines", arrayListOf<Map<String, Any>>())
        for (rawMine in rawMines) {
            mines.add(Mine.deserialize(rawMine))
        }
    }

    override fun dispose() {}

    @EventHandler(priority = EventPriority.LOWEST)
    @Suppress("UNCHECKED_CAST")
    fun onQuit(event: PlayerQuitEvent) {
        mines.forEach { it.onQuit(event.player) }
    }
}
