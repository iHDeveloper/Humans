package me.ihdeveloper.humans.core.api

import me.ihdeveloper.humans.core.System
import me.ihdeveloper.humans.core.entity.CustomNPC
import net.minecraft.server.v1_8_R3.Entity
import org.bukkit.Location

/**
 * an API used to link between the game's core and integrated part
 */
interface IntegrationAPI {
    /**
     * Systems of the integrated part
     */
    val systems: Array<System>

    /**
     * Custom entities registered by the integrated part
     */
    fun fromEntityType(type: String, location: Location): Entity?

    /**
     * Custom NPCs registered by the integrated part
     */
    fun fromNPCType(type: String, location: Location): CustomNPC?
}
