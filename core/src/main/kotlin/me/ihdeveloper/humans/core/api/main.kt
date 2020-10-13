package me.ihdeveloper.humans.core.api

import me.ihdeveloper.humans.service.GameTime
import me.ihdeveloper.humans.service.api.Profile
import org.bukkit.entity.Player

/**
 * Provides the ability to contact the game service server
 */
interface GameAPI {
    fun getTime(): GameTime

    fun getProfile(name: String): Profile?

    fun updateProfile(name: String, profile: Profile)
}
