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

/**
 * Represents a project to be funded by contributing the costs of the items
 */
class Project(
    val name: String,
    val displayName: String,
    val shortDescription: String,
    val description: Array<String>,
    val items: Array<ProjectItem>,
) {
    val contributions = IntArray(items.size) { 0 }
}

/**
 * Represents an item in the project with its cost to make
 */
data class ProjectItem(
    val name: String,
    val description: Array<String>,
    val material: String,
    val data: Short,
    val costId: String,
    val costAmount: Int,
    val maxAmount: Int,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ProjectItem

        if (name != other.name) return false
        if (!description.contentEquals(other.description)) return false
        if (material != other.material) return false
        if (data != other.data) return false
        if (maxAmount != other.maxAmount) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + description.contentHashCode()
        result = 31 * result + material.hashCode()
        result = 31 * result + data
        result = 31 * result + maxAmount
        return result
    }
}
