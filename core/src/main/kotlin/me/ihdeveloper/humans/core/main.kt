package me.ihdeveloper.humans.core

import com.google.gson.Gson
import java.io.File
import kotlin.concurrent.thread
import me.ihdeveloper.humans.core.api.GameAPI
import me.ihdeveloper.humans.core.api.IntegrationAPI
import me.ihdeveloper.humans.core.system.BlockSystem
import me.ihdeveloper.humans.core.system.BossBarSystem
import me.ihdeveloper.humans.core.system.ChatSystem
import me.ihdeveloper.humans.core.system.CommandSystem
import me.ihdeveloper.humans.core.system.CustomEntitySystem
import me.ihdeveloper.humans.core.system.FreezeSystem
import me.ihdeveloper.humans.core.system.GUISystem
import me.ihdeveloper.humans.core.system.ItemSystem
import me.ihdeveloper.humans.core.system.LoginSystem
import me.ihdeveloper.humans.core.system.MenuSystem
import me.ihdeveloper.humans.core.system.NPCSystem
import me.ihdeveloper.humans.core.system.PlayerSystem
import me.ihdeveloper.humans.core.system.ProfileSystem
import me.ihdeveloper.humans.core.system.RegionSystem
import me.ihdeveloper.humans.core.system.SceneSystem
import me.ihdeveloper.humans.core.system.ScoreboardSystem
import me.ihdeveloper.humans.core.system.TimeSystem
import me.ihdeveloper.humans.core.system.WarpSystem
import me.ihdeveloper.humans.core.util.GameLogger
import me.ihdeveloper.humans.service.api.GameTime
import me.ihdeveloper.spigot.devtools.api.DevTools
import org.bukkit.plugin.java.JavaPlugin

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

    /** An integrated part to the game to add more customizations */
    var integratedPart: IntegrationAPI? = null

    /** Global time of the game */
    var time = GameTime()

    /** Is the game ready to be open? */
    var isReady = false

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
        FreezeSystem(),
        ChatSystem(),
        RegionSystem(),
        GUISystem(),
        BossBarSystem()
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

        if (integratedPart == null) {
            logger.warn("----------------------------------------------")
            logger.warn("   THE CORE HAS NO INTEGRATED PART INCLUDED   ")
            logger.warn("  DON'T ALLOW THIS IN PRODUCTION ENVIRONMENT! ")
            logger.warn("----------------------------------------------")
        }

        if (api == null) {
            logger.error("NO GAME SERVICE API INSTANCE FOUND!")
            return
        }

        logger.info("Loading time from game service...")
        api!!.getTime {
            isReady = true
            time = it
            logger.info("Loaded! §7Time is $time")
            thread {
                time.start()
            }
        }

        logger.info("Initializing ${systems.size} core systems...")
        systems.forEach {
            logger.info("Initializing ${it.name.toLowerCase()} system...")
            it.init(plugin)
        }

        logger.info("Initializing ${integratedPart?.systems!!.size} integrated systems...")
        integratedPart?.systems!!.forEach {
            logger.info("Initializing ${it.name.toLowerCase()} system (integrated)...")
            it.init(plugin)
        }

        logger.info("Initializing ${otherSystems.size} other systems...")
        otherSystems.forEach {
            logger.info("Initializing ${it.name.toLowerCase()} system (other)...")
            it.init(plugin)
        }

        logger.info("Late Initializing ${systems.size} core systems...")
        systems.forEach {
            logger.info("Late initializing ${it.name.toLowerCase()} system...")
            it.lateInit(plugin)
        }

        logger.info("Late Initializing ${integratedPart?.systems!!.size} integrated systems...")
        integratedPart?.systems!!.forEach {
            logger.info("Late Initializing ${it.name.toLowerCase()} system (integrated)...")
            it.lateInit(plugin)
        }

        logger.info("Late Initializing ${otherSystems.size} other systems...")
        otherSystems.forEach {
            logger.info("Late Initializing ${it.name.toLowerCase()} system (other)...")
            it.lateInit(plugin)
        }

        DevTools.pin("Game World Name", serverName)
    }

    /**
     * Dispose the core of the game
     */
    fun dispose() {
        systems.forEach {
            logger.info("Disposing ${it.name.toLowerCase()} system...")
            it.dispose()
        }

        integratedPart?.systems!!.forEach {
            logger.info("Disposing ${it.name.toLowerCase()} system (other)...")
            it.dispose()
        }

        otherSystems.forEach {
            logger.info("Disposing ${it.name.toLowerCase()} system (other)...")
            it.dispose()
        }
    }
}