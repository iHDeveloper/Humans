package me.ihdeveloper.humans.core.system

import me.ihdeveloper.humans.core.System
import org.bukkit.plugin.java.JavaPlugin

/**
 * A system for handling and managing scenes in the game
 */
class SceneSystem : System("Core/Scene") {
    companion object {
        lateinit var plugin: JavaPlugin
    }

    override fun init(plugin: JavaPlugin) {
        Companion.plugin = plugin
    }

    override fun dispose() {}
}
