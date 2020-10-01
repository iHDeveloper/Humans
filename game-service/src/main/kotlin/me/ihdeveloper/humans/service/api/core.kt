package me.ihdeveloper.humans.service.api

data class Profile (
    val skills: Skills,
    val inventory: String
) {
    companion object
}

data class Skills (
    val miningLuckChance: Int
)
