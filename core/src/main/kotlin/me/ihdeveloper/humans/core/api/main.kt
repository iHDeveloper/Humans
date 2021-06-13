package me.ihdeveloper.humans.core.api

import me.ihdeveloper.humans.service.api.GameTime
import me.ihdeveloper.humans.service.api.Profile
import org.bukkit.entity.Player

/**
 * Provides the ability to contact the game service server
 */
interface GameAPI {

    /**
     * Returns the game's time
     */
    fun getTime(): GameTime

    /**
     * Returns the player's profile in the game
     */
    fun getProfile(name: String): Profile?

    /**
     * Updates the player's profile
     */
    fun updateProfile(name: String, profile: Profile)

    /**
     * Send a player to a different server
     */
    fun sendTo(player: Player, server: String)
}
