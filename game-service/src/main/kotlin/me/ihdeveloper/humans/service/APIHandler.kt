package me.ihdeveloper.humans.service

import me.ihdeveloper.humans.service.api.GameTime
import me.ihdeveloper.humans.service.api.Profile

interface APIHandler {
    fun getTime(): GameTime
    fun getProfile(name: String): Profile
    fun updateProfile(name: String, profile: Profile): Boolean
}