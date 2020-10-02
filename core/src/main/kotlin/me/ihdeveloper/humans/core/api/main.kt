package me.ihdeveloper.humans.core.api

import me.ihdeveloper.humans.service.GameTime
import me.ihdeveloper.humans.service.api.Profile
import org.bukkit.entity.Player

interface GameAPI {
    fun getTime(): GameTime

    suspend fun getProfile(player: Player): Profile?

    suspend fun updateProfile(player: Player)
}
