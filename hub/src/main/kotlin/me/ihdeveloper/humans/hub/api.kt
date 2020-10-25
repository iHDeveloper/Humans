package me.ihdeveloper.humans.hub

import me.ihdeveloper.humans.core.Command
import me.ihdeveloper.humans.core.SceneMeta
import me.ihdeveloper.humans.core.System
import me.ihdeveloper.humans.core.api.IntegrationAPI
import me.ihdeveloper.humans.core.entity.CustomNPC
import net.minecraft.server.v1_8_R3.Entity
import org.bukkit.Location
import org.bukkit.entity.Player

/**
 * Integrates game's hub with the game's core
 */
class HubIntegrationAPI : IntegrationAPI {
    override val allowNewPlayers = true

    override val commands = arrayOf<Command>()

    override val systems = arrayOf<System>()

    override fun fromEntityType(type: String, location: Location): Entity? = null

    override fun fromNPCType(type: String, location: Location): CustomNPC? = null

    override fun fromSceneName(name: String): SceneMeta? = null

    override fun playScene(player: Player, name: String): Boolean = false
}
