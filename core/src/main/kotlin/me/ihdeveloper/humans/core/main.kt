package me.ihdeveloper.humans.core

import org.bukkit.plugin.java.JavaPlugin

/** Utilities */
val logger = GameLogger("${COLOR_CYAN}Core")

/**
 * A main class to handle the bukkit plugin
 */
class Main : JavaPlugin() {
    override fun onEnable() {
        logger.info("Enabling the game core...")
    }

    override fun onDisable() {
        logger.info("Disabling the game core...")
    }
}
