package me.ihdeveloper.humans.mine

import me.ihdeveloper.humans.core.core
import me.ihdeveloper.humans.core.util.GameLogger
import org.bukkit.plugin.java.JavaPlugin

/**
 * The main class of the prison mine.
 *
 * It initializes the game core.
 * And, adds some special functionality for the mine to the core
 */
@Suppress("UNUSED")
class Main : JavaPlugin() {
    private val logger = GameLogger("Mine")

    override fun onEnable() {
        logger.info("Initializing core...")

        core.serverName = "Mine"
        core.integratedPart = MineIntegrationAPI()
        core.init(this)
    }

    override fun onDisable() {
        logger.info("Disposing core...")

        core.dispose()
    }
}
