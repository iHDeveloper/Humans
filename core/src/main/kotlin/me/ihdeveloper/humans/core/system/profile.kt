package me.ihdeveloper.humans.core.system

import me.ihdeveloper.humans.core.System
import me.ihdeveloper.humans.core.core
import me.ihdeveloper.humans.service.api.Profile
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin

/**
 * A system for handling profiles
 */
class ProfileSystem : System("Core/Profile"), Listener {
    private data class InventoryItemInfo(
        val amount: Int?,
        val data: String?
    )

    companion object {
        val profiles = mutableMapOf<String, Profile>()
    }

    override fun init(plugin: JavaPlugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin)
    }

    override fun dispose() {}

    /**
     * Loads the player's profile into memory
     */
    @EventHandler(priority = EventPriority.LOWEST)
    @Suppress("UNUSED")
    fun onLogin(event: PlayerLoginEvent) {
        event.player.run {
            logger.info("$name is logging in...")

            val profile = core.api!!.getProfile(name)

            /** This error occurs when the api failed to fetch profile */
            if (profile === null) {
                logger.error("§cUnable to load ${name}'s profile!")
                event.disallow(
                    PlayerLoginEvent.Result.KICK_WHITELIST,
                    "§cFailed to connect to Humans! §7(PROFILE_NOT_FOUND)"
                )
                return
            }

            profiles[name] = profile

            logger.info("$name logged in!")
        }
    }

    /**
     * Loads the items from the player's profile inventory to the actual "inventory"
     */
    @EventHandler(priority = EventPriority.LOWEST)
    @Suppress("UNUSED")
    fun onJoin(event: PlayerJoinEvent) {
        // TODO Read inventory data and load it into the player
    }

    /**
     * Update the player's inventory immediately
     */
    @EventHandler(priority = EventPriority.LOWEST)
    @Suppress("UNUSED")
    fun onQuit(event: PlayerQuitEvent) {
        // TODO Write inventory data from player and store it in the service
    }

}