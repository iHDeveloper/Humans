package me.ihdeveloper.humans.core

import me.ihdeveloper.humans.core.entity.Hologram
import me.ihdeveloper.humans.core.entity.WarpCart
import org.bukkit.Location
import org.bukkit.entity.Player

val warps = mutableListOf<Warp>()
val warpsInfo = mutableListOf<WarpInfo>()

/**
 * Represents information about the warp to be used in the [Configuration]
 */
data class WarpInfo(
    val displayName: String,
    val center: Location,
    val start: Location,
    val end: Location
): ConfigurationSerialize {
    companion object: ConfigurationDeserialize<WarpInfo> {
        override fun deserialize(data: Map<String, Any>): WarpInfo = WarpInfo(
            displayName = data["displayName"] as String,
            center = data["center"] as Location,
            start = data["start"] as Location,
            end = data["end"] as Location
        )
    }

    override fun serialize(): Map<String, Any> = mapOf(
        "displayName" to displayName,
        "center" to center,
        "start" to start,
        "end" to end
    )
}

/**
 * Manages the warp gate and its entities
 */
class Warp(
    private val displayName: String,
    private val center: Location,
    private val start: Location,
    private val end: Location
) {
    companion object {
        fun fromInfo(info: WarpInfo) = Warp(
            displayName = info.displayName,
            center = info.center,
            start = info.start,
            end = info.end
        )
    }

    private val from = center.clone().subtract(2.0, 0.0, 0.0)
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
    fun check(player: Player) = player.location.between(from, to)

    /**
     * Creates the mine cart entity. And rides the player on it.
     * And, let the mine cart move.
     */
    fun join(player: Player) {
        val cart = WarpCart(player.entityId, start, end)
        carts.add(cart)

        spawnEntity(cart, false, null)
        cart.bukkitEntity.passenger = player
    }
}
