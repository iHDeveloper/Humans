package me.ihdeveloper.humans.core.entity

import net.minecraft.server.v1_8_R3.DamageSource
import net.minecraft.server.v1_8_R3.Entity
import net.minecraft.server.v1_8_R3.ItemStack
import net.minecraft.server.v1_8_R3.Items
import org.bukkit.Location

class PrisonGuard(location: Location)
    : CustomSkeleton(location) {

    init {
        setLocation()

        clearPathfinderSelector(goalSelector)
        clearPathfinderSelector(targetSelector)

        setEquipment(0, ItemStack(Items.BOW))

        nameHologram.text = "§cPrison Guard"
        spawnHologram()
    }

    /**
     * Prevent the guard from burning by fire
     */
    override fun m() {
        return
    }

    /**
     * Prevent the guard from moving
     */
    override fun move(d0: Double, d1: Double, d2: Double) {
        return
    }

    /**
     * Prevent the guard from colliding with anyone
     */
    override fun collide(entity: Entity?) {
        return
    }

    /**
     * Prevent the guard from getting damaged
     */
    override fun damageEntity(damagesource: DamageSource?, f: Float): Boolean {
        return false
    }

    /**
     * Prevent the guard from moving
     */
    override fun g(d0: Double, d1: Double, d2: Double) {
        return
    }

    /**
     * Prevent the guard from attacking anyone. If a bow is in the guard's hand
     */
    override fun n() {
        return
    }

}
