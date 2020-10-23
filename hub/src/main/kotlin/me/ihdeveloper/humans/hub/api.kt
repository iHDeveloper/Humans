package me.ihdeveloper.humans.hub

import me.ihdeveloper.humans.core.System
import me.ihdeveloper.humans.core.api.IntegrationAPI
import me.ihdeveloper.humans.core.entity.CustomNPC
import net.minecraft.server.v1_8_R3.Entity
import org.bukkit.Location

/**
 * Integrates game's hub with the game's core
 */
class HubIntegrationAPI : IntegrationAPI {
    override val systems = arrayOf<System>()

    override fun fromEntityType(type: String, location: Location): Entity? = null

    override fun fromNPCType(type: String, location: Location): CustomNPC? = null
}
