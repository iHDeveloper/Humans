package me.ihdeveloper.humans.core

import me.ihdeveloper.humans.core.system.BlockSystem
import me.ihdeveloper.humans.core.system.CommandSystem
import me.ihdeveloper.humans.core.system.CustomEntitySystem
import org.bukkit.plugin.java.JavaPlugin

/** An instance of game core */
val core = GameCore()

/** An instance of game core plugin */
var corePlugin: JavaPlugin? = null

/** Utilities */
private val logger = GameLogger("Core")

/**
 * A main class to handle the bukkit plugin
 *
 * Note: It doesn't initialize the core.
 */
class Main : JavaPlugin() {

    override fun onEnable() {
        corePlugin = this
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
    private val systems = arrayOf<System>(
        BlockSystem(),
        CommandSystem(),
        CustomEntitySystem()
    )

    /**
     * Initialize the core of the game
     */
    fun init(plugin: JavaPlugin) {
        if (plugin === corePlugin) {
            logger.warn("----------------------------------------------")
            logger.warn("  THE CORE IS BEING INITIALIZED USING ITSELF  ")
            logger.warn("   DON'T USE THIS IN PRODUCTION ENVIRONMENT!  ")
            logger.warn("----------------------------------------------")
        }

        systems.forEach {
            logger.info("Initializing ${it.name.toLowerCase()} system...")
            it.init(plugin)
        }
    }

    /**
     * Dispose the core of the game
     */
    fun dispose() {
        systems.forEach {
            logger.info("Disposing ${it.name.toLowerCase()} system...")
            it.dispose()
        }
    }
}