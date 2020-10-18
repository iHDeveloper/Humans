package me.ihdeveloper.humans.core

import com.google.gson.Gson
import me.ihdeveloper.humans.core.api.GameAPI
import me.ihdeveloper.humans.core.system.BlockSystem
import me.ihdeveloper.humans.core.system.CommandSystem
import me.ihdeveloper.humans.core.system.CustomEntitySystem
import me.ihdeveloper.humans.core.system.ItemSystem
import me.ihdeveloper.humans.core.system.LoginSystem
import me.ihdeveloper.humans.core.system.MenuSystem
import me.ihdeveloper.humans.core.system.NPCSystem
import me.ihdeveloper.humans.core.system.PlayerSystem
import me.ihdeveloper.humans.core.system.ScoreboardSystem
import me.ihdeveloper.humans.core.system.TimeSystem
import me.ihdeveloper.humans.core.system.WarpSystem
import me.ihdeveloper.humans.service.GameTime
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import kotlin.concurrent.thread
import me.ihdeveloper.humans.core.system.ProfileSystem
import me.ihdeveloper.humans.core.system.SceneSystem
import me.ihdeveloper.humans.core.util.GameLogger

/** Folder that contains data about the plugin */
val dataFolder = File("plugins/Humans").also { it.mkdir() }

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
        if (!dataFolder.isDirectory) {
            dataFolder.mkdir()
        }

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

    /** Represents the server name */
    var serverName: String? = null

    /** An instance to use the game service API */
    var api: GameAPI? = null

    /** Global time of the game */
    var time = GameTime()

    /** An instance of Gson */
    val gson = Gson()

    /** Represents the core systems of the game */
    private val systems = arrayOf(
        TimeSystem(),
        ItemSystem(),
        ProfileSystem(),
        LoginSystem(),
        BlockSystem(),
        CommandSystem(),
        MenuSystem(),
        CustomEntitySystem(),
        PlayerSystem(),
        WarpSystem(),
        ScoreboardSystem(),
        NPCSystem(),
        SceneSystem(),
    )

    /** Represents other systems of the game */
    val otherSystems = arrayListOf<System>()

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

        if (api == null) {
            logger.error("NO GAME SERVICE API INSTANCE FOUND!")
            return
        }

        val newTime = api!!.getTime()
        if (newTime === time) {
            logger.error("Failed to fetch the updated time from the API!")
            // TODO Change the server state to ERROR
        } else {
            time = newTime
            thread {
                time.start()
            }
        }

        systems.forEach {
            logger.info("Initializing ${it.name.toLowerCase()} system...")
            it.init(plugin)
        }

        otherSystems.forEach {
            logger.info("Initializing ${it.name.toLowerCase()} system (other)...")
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