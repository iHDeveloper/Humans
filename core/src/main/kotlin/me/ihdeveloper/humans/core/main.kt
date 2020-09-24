package me.ihdeveloper.humans.core

import org.bukkit.plugin.java.JavaPlugin

/** An instance of game core */
val core = GameCore()

/** Utilities */
private val logger = GameLogger("${COLOR_CYAN}Core")

/**
 * A main class to handle the bukkit plugin
 *
 * Note: It doesn't initialize the core.
 */
class Main : JavaPlugin() {

    override fun onEnable() {
        logger.info("Core is ready to use!")
    }

    override fun onDisable() {
        logger.info("Core is disabled!")
    }
}

/**
 * The core of the game
 */
class GameCore {
    /** Represents the core systems of the game */
    private val systems = arrayOf<System>()

    /**
     * Initialize the core of the game
     */
    fun init(plugin: JavaPlugin) {
        logger.info("Initializing the systems...")
        systems.forEach { it.init(plugin) }
    }

    /**
     * Dispose the core of the game
     */
    fun dispose() {
        logger.info("Disposing the systems...")
        systems.forEach { it.dispose() }
    }
}