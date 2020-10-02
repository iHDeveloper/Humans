package me.ihdeveloper.humans.core.api

import me.ihdeveloper.humans.service.GameTime
import me.ihdeveloper.humans.service.api.Profile
import org.bukkit.entity.Player

interface GameAPI {
    fun getTime(): GameTime

    fun getProfile(player: Player): Profile

    fun updateProfile(player: Player)
}
