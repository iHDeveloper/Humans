package me.ihdeveloper.humans.service.api

import com.google.gson.JsonObject

/**
 * Represents the player's profile in the game
 */
data class Profile (
    var skills: Skills,
    var inventory: Map<Int, JsonObject>,
    var new: Boolean,
) {
    companion object
}

/**
 * Represents the player's skills in the game
 */
data class Skills (
    var miningLuckChance: Int = 1
)
