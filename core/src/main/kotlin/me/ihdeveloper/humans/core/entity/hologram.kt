package me.ihdeveloper.humans.core.entity

import me.ihdeveloper.humans.core.spawnEntity
import net.minecraft.server.v1_8_R3.DamageSource
import net.minecraft.server.v1_8_R3.EntityHuman
import net.minecraft.server.v1_8_R3.ItemStack
import net.minecraft.server.v1_8_R3.Vec3D
import org.bukkit.Location

/**
 * A hologram to display a 3D text in the world
 */
class Hologram (
        location: Location,
        private val text: String
) : CustomArmorStand(location) {

    /** Prevent the player from putting any item to the hologram */
    override fun d(i: Int, itemstack: ItemStack?): Boolean {
        return false
    }

    /** Prevent the hologram from colliding with the mine cart */
    override fun bL() {
        return
    }

    /** Prevent the player from manipulating the hologram */
    override fun a(entityhuman: EntityHuman?, vec3d: Vec3D?): Boolean {
        return false
    }

    /** Prevent the hologram from getting damaged */
    override fun damageEntity(damagesource: DamageSource?, f: Float): Boolean {
        return false
    }

    override fun isInvisible(): Boolean {
        return true
    }

    override fun isBaby(): Boolean {
        return false
    }

    override fun hasGravity(): Boolean {
        return false
    }

    override fun getCustomNameVisible(): Boolean {
        return true
    }

    override fun getCustomName(): String {
        return text
    }
}

/**
 * Spawns a hologram for a NPC
 * It consists of name and type of the npc
 *
 * @param location The npc location
 * @param name The name of the npc
 * @param type The type of the npc (e.g. Prison Guard, Prisoner)
 * @param state The state of the npc (e.g. Click, Coming soon)
 */
fun spawnNPCHologram(location: Location, name: String, type: String, state: String) {
    location.clone().apply {
        y += y + 1.0

        spawnEntity(Hologram(this, state), false)

        y += 0.50
        spawnEntity(Hologram(this, type), false)

        y += 0.25
        spawnEntity(Hologram(this, name), false)
    }
}
