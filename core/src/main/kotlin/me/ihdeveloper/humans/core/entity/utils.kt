package me.ihdeveloper.humans.core.entity

import net.minecraft.server.v1_8_R3.Entity
import org.bukkit.Location

/**
 * Generating an entity class depending on the type
 */
fun fromEntityType(type: String, location: Location): Entity? = when(type) {
    "prison_guard" -> PrisonGuard(location)
    "hologram" -> Hologram(location, "Text")
    else -> null
}
