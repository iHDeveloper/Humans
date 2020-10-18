package me.ihdeveloper.humans.core.entity

import kotlin.math.sqrt
import me.ihdeveloper.humans.core.registry.spawnEntity
import me.ihdeveloper.humans.core.util.NMSItemStack
import net.minecraft.server.v1_8_R3.DamageSource
import net.minecraft.server.v1_8_R3.EntityHuman
import net.minecraft.server.v1_8_R3.EntityLiving
import net.minecraft.server.v1_8_R3.ItemStack
import net.minecraft.server.v1_8_R3.Items
import net.minecraft.server.v1_8_R3.MovingObjectPosition
import net.minecraft.server.v1_8_R3.Vec3D
import org.bukkit.Location
import org.bukkit.World

/**
 * A guard that protects the prison
 *
 * Currently it has no action to do. Instead of being a "guard"
 */
class PrisonGuard(
    location: Location
) : CustomSkeleton(location) {

    init {
        setLocation()

        clearPathfinderSelector(goalSelector)
        clearPathfinderSelector(targetSelector)

        setEquipment(0, ItemStack(Items.IRON_SWORD))
        setEquipment(2, ItemStack(Items.LEATHER_CHESTPLATE))
        setEquipment(3, ItemStack(Items.LEATHER_LEGGINGS))

        nameHologram.text = "§cPrison Guard"
        spawnHologram()
    }

}

/**
 * Watches the player and freezes it
 */
class PrisonWatcher(
    location: Location
) : CustomArmorStand(location) {

    init {
        customName = "§cPrison Watcher"
        customNameVisible = true
        isInvisible = true
        setGravity(false)

        equipment[1] = ItemStack(Items.SKULL)
    }

    /** Prevent the player from putting any item to the prison watcher */
    override fun d(i: Int, itemstack: NMSItemStack?): Boolean {
        return false
    }

    /** Prevent the prison watcher from colliding with the mine cart */
    override fun bL() {
        return
    }

    /** Prevent the player from manipulating the prison watcher */
    override fun a(entityhuman: EntityHuman?, vec3d: Vec3D?): Boolean {
        return false
    }

    /** Prevent the prison watcher from getting damaged */
    override fun damageEntity(damagesource: DamageSource?, f: Float): Boolean {
        return false
    }

}

/**
 * A witch that manipulates the humans in the prison
 */
class PrisonWitch(
    private val location: Location
) : CustomWitch(location) {
    class Potion(
        world: World,
        witch: PrisonWitch,
    ) : CustomPotion(world, witch, 32696 /* Weakness Potion */) {

        override fun a(movingobjectposition: MovingObjectPosition?) {
//            this.die()
//            return
        }

    }

    init {
        setLocation()

        clearPathfinderSelector(goalSelector)
        clearPathfinderSelector(targetSelector)

        nameHologram.text = "§cPrison Witch"
        spawnHologram()
    }

    fun shoot(target: EntityLiving) {
        val potion = Potion(location.world, this)
        val targetY = target.locY + target.headHeight.toDouble() - 1.100000023841858
        potion.pitch -= -20F
        val x = target.locX + target.motX - locX
        val y = targetY - locY
        val z = target.locZ + target.motZ - locZ
        val distance = sqrt((x * x) + (z * z))

        potion.shoot(x, y + (distance * 0.2F), z, 0.75F, 0.75F)
        spawnEntity(potion, true, null)
    }

}
