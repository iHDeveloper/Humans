package me.ihdeveloper.humans.service

import me.ihdeveloper.humans.service.api.GameTime
import me.ihdeveloper.humans.service.api.Profile
import me.ihdeveloper.humans.service.api.Skills

internal class InternalAPIHandler(
    private val gameTime: GameTime
) : APIHandler {
    private val profiles = mutableMapOf<String, Profile>()

    override fun getTime(): GameTime {
        return gameTime
    }

    override fun getProfile(name: String): Profile {
        return profiles[name] ?: Profile(Skills(), mutableMapOf(), true)
    }

    override fun updateProfile(name: String, profile: Profile): Boolean {
        if (!profiles.contains(name))
            return false

        profile.new = false
        profiles[name] = profile
        return true
    }
}