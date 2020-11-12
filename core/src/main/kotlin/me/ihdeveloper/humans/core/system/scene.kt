package me.ihdeveloper.humans.core.system

import me.ihdeveloper.humans.core.System
import me.ihdeveloper.humans.core.scene.IndividualScene
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin

/**
 * A system for handling and managing scenes in the game
 */
class SceneSystem : System("Core/Scene"), Listener {
    companion object {
        lateinit var plugin: JavaPlugin

        /** A set of players who are watching individual scenes */
        val individuals = mutableMapOf<String, IndividualScene>()
    }

    override fun init(plugin: JavaPlugin) {
        Companion.plugin = plugin

        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    override fun dispose() {}

    @EventHandler
    @Suppress("UNUSED")
    fun onQuit(event: PlayerQuitEvent) {
       individuals.remove(event.player.name)
    }
}
