package me.ihdeveloper.humans.hub

import me.ihdeveloper.humans.core.core
import me.ihdeveloper.humans.core.util.GameLogger
import org.bukkit.plugin.java.JavaPlugin

/**
 * The main class of the prison hub.
 *
 * It initializes the game core.
 * And, adds some special functionality for the hub to the core
 */
class Main : JavaPlugin() {
    private val logger = GameLogger("Hub")

    override fun onEnable() {
        logger.info("Initializing core...")

        core.serverName = "Hub"
        core.integratedPart = HubIntegrationAPI()
        core.init(this)
    }

    override fun onDisable() {
        logger.info("Disposing core...")

        core.dispose()
    }
}
