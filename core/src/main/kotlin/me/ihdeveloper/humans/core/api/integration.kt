package me.ihdeveloper.humans.core.api

import me.ihdeveloper.humans.core.Command
import me.ihdeveloper.humans.core.SceneMeta
import me.ihdeveloper.humans.core.System
import me.ihdeveloper.humans.core.entity.CustomNPC
import net.minecraft.server.v1_8_R3.Entity
import org.bukkit.Location
import org.bukkit.entity.Player

/**
 * an API used to link between the game's core and integrated part
 */
interface IntegrationAPI {
    /**
     * Returns if the integrated part supports new players situations or not
     */
    val allowNewPlayers: Boolean

    /**
     * Systems of the integrated part
     */
    val systems: Array<System>

    /**
     * Commands of the integrated part
     */
    val commands: Array<Command>

    /**
     * Custom entities registered by the integrated part
     */
    fun fromEntityType(type: String, location: Location): Entity?

    /**
     * Custom NPCs registered by the integrated part
     */
    fun fromNPCType(type: String, location: Location): CustomNPC?

    /**
     * Returns the [SceneMeta] of the given name
     */
    fun fromSceneName(name: String): SceneMeta?

    /**
     * Initiates an instance of the scene from the given name and starts it
     */
    fun playScene(player: Player, name: String): Boolean
}
