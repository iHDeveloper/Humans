package me.ihdeveloper.humans.core

import me.ihdeveloper.humans.core.entity.Hologram
import me.ihdeveloper.humans.core.entity.WarpCart
import me.ihdeveloper.humans.core.registry.spawnEntity
import me.ihdeveloper.humans.core.util.between
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.util.Vector

val warps = mutableListOf<Warp>()
val warpsInfo = mutableListOf<WarpInfo>()

/**
 * Represents information about the warp to be used in the [Configuration]
 */
data class WarpInfo(
    val displayName: String,
    val center: Location,
    val start: Location,
    val end: Location,

    /** The server to transfer the player to */
    val server: String? = null,
) {
    companion object {
        fun deserialize(data: Map<String, Any?>): WarpInfo = WarpInfo(
            displayName = data["displayName"] as String,
            center = data["center"] as Location,
            start = data["start"] as Location,
            end = data["end"] as Location,
            server = if (data["server"] != null) data["server"] as String else null,
        )
    }

    fun serialize(): Map<String, Any?> = mapOf(
        "displayName" to displayName,
        "center" to center,
        "start" to start,
        "end" to end,
        "server" to server,
    )
}

/**
 * Manages the warp gate and its entities
 */
class Warp(
    private val displayName: String,
    private val center: Location,
    val start: Location,
    val end: Location,
    private val server: String? = null
) {
    companion object {
        fun fromInfo(info: WarpInfo) = Warp(
            displayName = info.displayName,
            center = info.center,
            start = info.start,
            end = info.end,
            server = info.server
        )
    }

    val spawn: Location = center.clone().subtract(0.5, 0.0, 2.5)
    private val from = center.clone().add(2.0, 0.0, 0.0)
    private val to = center.clone().add(-2.0, 4.0, 0.0)
    private val carts = arrayListOf<WarpCart>()
    private var holograms: Array<Hologram>? = null

    fun init() {
        center.clone().apply {
            add(0.5, 1.0, 0.5)

            val title = Hologram(this, "§eDestination:")

            subtract(0.0, 0.25, 0.0)
            val destination = Hologram(this, displayName)

            spawnEntity(title, false, null)
            spawnEntity(destination, false, null)

            holograms = arrayOf(title, destination)
        }
    }

    fun dispose() {
        holograms!!.forEach { it.die() }
    }

    /**
     * Check if the player is in front of the gate
     */
    fun check(player: Player) = player.location.block.location.between(from, to)

    /**
     * Creates the mine cart entity. And rides the player on it.
     * And, let the mine cart move.
     */
    fun join(player: Player) {
        val cart = WarpCart(player.entityId, start, this)
        carts.add(cart)

        spawnEntity(cart, false, null)
        cart.apply {
            bukkitEntity.passenger = player

            // Fix: Cart doesn't move on the start
            bukkitEntity.velocity = Vector(0.45, 0.0, 0.0)
        }

        server?.run {
            Bukkit.getScheduler().runTaskLater(corePlugin, {
                core.api!!.sendTo(player, this)
            }, 30L)
        }
    }
}
