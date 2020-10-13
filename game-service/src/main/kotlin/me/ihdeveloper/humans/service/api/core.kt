package me.ihdeveloper.humans.service.api

/**
 * Represents the player's profile in the game
 */
data class Profile (
    var skills: Skills,
    var inventory: String,
    var new: Boolean,
) {
    companion object
}

/**
 * Represents the player's skills in the game
 */
data class Skills (
    val miningLuckChance: Int
)
