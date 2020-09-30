package me.ihdeveloper.humans.core.entity

import net.minecraft.server.v1_8_R3.DamageSource
import net.minecraft.server.v1_8_R3.EntityHuman
import org.bukkit.Location

/**
 * A special cart for player using the warp gate
 */
class WarpCart(
    private val playerEntityId: Int,
    val start: Location,
    val end: Location,
) : CustomMineCart(start) {

    init {
        setLocation(start.x, start.y, start.z, start.yaw, start.pitch)
    }

    /**
     * Allow the player who requested to go to the warp.
     * Prevent the other players who didn't request to go.
     */
    override fun e(p0: EntityHuman?): Boolean {
        if (p0!!.id != playerEntityId)
            return false

        return super.e(p0)
    }

    /**
     * Prevent the player from dismounting the cart
     */
    override fun a(p0: Int, p1: Int, p2: Int, p3: Boolean) {
        return
    }

    /**
     * Prevent the cart from getting damaged
     */
    override fun damageEntity(damagesource: DamageSource?, f: Float): Boolean {
        return false
    }
}
