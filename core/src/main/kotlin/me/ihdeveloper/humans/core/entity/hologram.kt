package me.ihdeveloper.humans.core.entity

import me.ihdeveloper.humans.core.GameLogger
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
        text: String = "Null"
) : CustomArmorStand(location) {

    var text = "Null"
        set (value) {
            customName = value
            field = value
        }

    init {
        this.text = text
        customNameVisible = true
        isInvisible = true
        isSmall = true
        setGravity(false)
    }

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
fun spawnNPCHologram(location: Location, name: String, type: String, state: String): Array<Hologram> = spawnNPCHologram(location, name, type, state)
fun spawnNPCHologram(location: Location, name: String, type: String, state: String, logger: GameLogger?): Array<Hologram> {
    location.clone().apply {
        y += 1.0

        val stateHologram = Hologram(this, state)
        spawnEntity(stateHologram, false, logger)

        y += 0.40
        val typeHologram = Hologram(this, type)
        spawnEntity(typeHologram, false, logger)

        y += 0.25
        val nameHologram = Hologram(this, name)
        spawnEntity(nameHologram, false, logger)
        return arrayOf(nameHologram, typeHologram, stateHologram)
    }
}
