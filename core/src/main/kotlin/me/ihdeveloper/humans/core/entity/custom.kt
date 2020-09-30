package me.ihdeveloper.humans.core.entity

import me.ihdeveloper.humans.core.setPrivateField
import me.ihdeveloper.humans.core.spawnEntity
import me.ihdeveloper.humans.core.toMinecraftWorld
import net.minecraft.server.v1_8_R3.EntityArmorStand
import net.minecraft.server.v1_8_R3.EntityMinecartRideable
import net.minecraft.server.v1_8_R3.EntitySkeleton
import net.minecraft.server.v1_8_R3.PathfinderGoalSelector
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_8_R3.util.UnsafeList

/**
 * An custom entity for armor stand
 */
open class CustomArmorStand(location: Location)
    : EntityArmorStand(toMinecraftWorld(location.world), location.x, location.y, location.z) {

    /**
     * Fix: Gravity flag value is reversed
     */
    override fun setGravity(flag: Boolean) {
        super.setGravity(!flag)
    }

}

open class CustomSkeleton(
        private val location: Location
) : EntitySkeleton(toMinecraftWorld(location.world)) {

    protected val nameHologram = Hologram(location.clone().apply {y += 1}, "§cSkeleton")

    /**
     * Updates the location of the entity in the world
     *
     * Note: It should be used during the initialization of the final class
     */
    fun setLocation() {
        setLocation(location.x, location.y, location.z, location.yaw, location.pitch)
    }

    /**
     * Clears [PathfinderGoalSelector] using Reflection
     */
    fun clearPathfinderSelector(selector: PathfinderGoalSelector) {
        setPrivateField(selector, "b", UnsafeList<PathfinderGoalSelector>())
        setPrivateField(selector, "c", UnsafeList<PathfinderGoalSelector>())
    }

    /**
     * Spawns the name hologram
     */
    fun spawnHologram() {
        spawnEntity(nameHologram, false, null)
    }

    /**
     * Remove the hologram when the entity dies
     */
    override fun die() {
        super.die()
        nameHologram.bukkitEntity.remove()
    }

}

/**
 * Custom entity for rideable mine cart
 */
open class CustomMineCart(location: Location)
    : EntityMinecartRideable(toMinecraftWorld(location.world))
