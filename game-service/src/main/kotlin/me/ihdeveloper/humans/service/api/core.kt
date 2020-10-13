package me.ihdeveloper.humans.service.api

/**
 * Represents the player's profile in the game
 */
data class Profile (
    val skills: Skills,
    val inventory: String,
    val new: Boolean,
) {
    companion object
}

/**
 * Represents the player's skills in the game
 */
data class Skills (
    val miningLuckChance: Int
)
