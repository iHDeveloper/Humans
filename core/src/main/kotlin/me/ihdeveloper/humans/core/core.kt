package me.ihdeveloper.humans.core

import org.bukkit.plugin.java.JavaPlugin

/**
 * Represents a system in the game.
 *
 * It can be initialized and disposed. It contains a special logger.
 */
abstract class System(name: String) {
    protected val logger = GameLogger(name)

    /**
     * Initialize the system
     */
    abstract fun init(plugin: JavaPlugin)

    /**
     * Dispose the system
     */
    abstract fun dispose()
}