package me.ihdeveloper.humans.core.entity

import kotlin.math.sqrt
import me.ihdeveloper.humans.core.corePlugin
import me.ihdeveloper.humans.core.registry.spawnEntity
import me.ihdeveloper.humans.core.scene.IntroScene
import me.ihdeveloper.humans.core.system.SceneSystem
import me.ihdeveloper.humans.core.util.NMSItemStack
import me.ihdeveloper.humans.core.util.findEntities
import net.minecraft.server.v1_8_R3.BlockPosition
import net.minecraft.server.v1_8_R3.DamageSource
import net.minecraft.server.v1_8_R3.EntityHuman
import net.minecraft.server.v1_8_R3.EntityLiving
import net.minecraft.server.v1_8_R3.EntityPlayer
import net.minecraft.server.v1_8_R3.Items
import net.minecraft.server.v1_8_R3.MovingObjectPosition
import net.minecraft.server.v1_8_R3.PathfinderGoalLookAtPlayer
import net.minecraft.server.v1_8_R3.Vec3D
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.ArmorStand
import org.bukkit.inventory.ItemStack

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

        setEquipment(0, NMSItemStack(Items.IRON_SWORD))
        setEquipment(2, NMSItemStack(Items.LEATHER_CHESTPLATE))
        setEquipment(3, NMSItemStack(Items.LEATHER_LEGGINGS))

        nameHologram.text = "§cPrison Guard"
        spawnHologram()
    }

}

/**
 * Watches the player and freezes it
 */
class PrisonWatcher(
    location: Location
) : CustomArmorStand(location), Runnable {
    private val maxY = location.y + 1.0
    private val minY = location.y - 1.0

    /** up = false, down = true */
    private var direction = false

    init {
        customName = "§cPrison Watcher"
        customNameVisible = true
        isInvisible = true
        setGravity(false)
        setLocation()

        (getBukkitEntity() as ArmorStand).run {
            helmet = ItemStack(Material.SKULL_ITEM)
        }
        Bukkit.getScheduler().runTaskTimer(corePlugin, this, 0L, 1L)
    }

    /** Move the body down and up every 1 tick */
    override fun run() {
        if (direction) {
            if (location.y <= minY) {
                direction = false
            } else {
                location.add(0.0, -0.10,0.0)
            }
        } else {
            if (location.y >= maxY) {
                direction = true
            } else {
                location.add(0.0, 0.10,0.0)
            }
        }
        setLocation()
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
    var playerName: String? = null

    class Potion(
        world: World,
        witch: PrisonWitch,
        private val playerName: String?,
    ) : CustomPotion(world, witch, 32696 /* Weakness Potion */) {

        override fun a(movingobjectposition: MovingObjectPosition?) {
            val boundingBox = boundingBox.grow(4.0, 4.0, 4.0)

            if (playerName != null) {
                val entities = world.findEntities(EntityPlayer::class, boundingBox)
                for (entity in entities) {
                    if (entity.name != playerName)
                        continue

                    val scene = SceneSystem.players[playerName] ?: break

                    if (scene !is IntroScene)
                        break

                    scene.resume()
                }
            }

            /** Show potion effect particles on the world */
            world.triggerEffect(2002, BlockPosition(this), potionValue)

            die()
            return
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
        val potion = Potion(location.world, this, playerName)
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
