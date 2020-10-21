package me.ihdeveloper.humans.core.entity

import kotlin.math.sqrt
import me.ihdeveloper.humans.core.corePlugin
import me.ihdeveloper.humans.core.scene.IntroScene
import me.ihdeveloper.humans.core.system.SceneSystem
import me.ihdeveloper.humans.core.util.NMSItemStack
import me.ihdeveloper.humans.core.util.findEntities
import me.ihdeveloper.humans.core.util.toNMS
import net.minecraft.server.v1_8_R3.BlockPosition
import net.minecraft.server.v1_8_R3.DamageSource
import net.minecraft.server.v1_8_R3.EntityHuman
import net.minecraft.server.v1_8_R3.EntityLiving
import net.minecraft.server.v1_8_R3.EntityPlayer
import net.minecraft.server.v1_8_R3.Items
import net.minecraft.server.v1_8_R3.MathHelper
import net.minecraft.server.v1_8_R3.MovingObjectPosition
import net.minecraft.server.v1_8_R3.NBTTagCompound
import net.minecraft.server.v1_8_R3.PacketPlayOutEntity
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityEquipment
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityHeadRotation
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityTeleport
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntity
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving
import net.minecraft.server.v1_8_R3.PacketPlayOutUpdateEntityNBT
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldEvent
import net.minecraft.server.v1_8_R3.Vec3D
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.ArmorStand
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitTask

private const val WATCHER_Y_RANGE = 0.5
private const val WATCHER_Y_SPEED = 0.1

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
    var isAnimating = false

    private val maxY = location.y + WATCHER_Y_RANGE
    private val minY = location.y - WATCHER_Y_RANGE

    /** up = false, down = true */
    private var direction = false

    /** Used to calculate the Y difference for sending packets */
    private var diffY: Double = 0.0

    private var animationTask: BukkitTask? = null

    init {
        customName = "§cPrison Watcher"
        customNameVisible = true
        isInvisible = true
        setGravity(false)
        setLocation()

        (getBukkitEntity() as ArmorStand).run {
            helmet = ItemStack(Material.SKULL_ITEM)
        }
    }

    /** Starts the loop for the animation */
    fun startAnimation() {
        isAnimating = true

        animationTask = Bukkit.getScheduler().runTaskTimer(corePlugin, this, 0L, 1L)
    }

    /** Move the body down and up every 1 tick */
    override fun run() {
        if (direction) {
            if (location.y <= minY) {
                direction = false
            } else {
                diffY = -WATCHER_Y_SPEED
                location.add(0.0, -WATCHER_Y_SPEED,0.0)
            }
        } else {
            if (location.y >= maxY) {
                direction = true
            } else {
                diffY = WATCHER_Y_SPEED
                location.add(0.0, WATCHER_Y_SPEED,0.0)
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

    override fun die() {
        super.die()

        animationTask?.cancel()
    }


    /** Methods that deals with the packet layer */


    fun spawnToPlayer(player: EntityPlayer) {
        player.connection.also {
            it.sendPacket(PacketPlayOutSpawnEntityLiving(this))
            val pitch = MathHelper.d(location.pitch * 256.0f / 360.0f).toByte()
            val yaw = MathHelper.d(location.yaw * 256.0f / 360.0f).toByte()
            it.sendPacket(PacketPlayOutEntity.PacketPlayOutEntityLook(id, yaw, pitch, false))
            it.sendPacket(PacketPlayOutEntityHeadRotation(this, yaw))
            it.sendPacket(PacketPlayOutUpdateEntityNBT(id, NBTTagCompound().apply { b(this) }))

            for (slot in 0..4) {
                it.sendPacket(PacketPlayOutEntityEquipment(id, slot, if (slot == 4) NMSItemStack(Items.SKULL) else null))
            }
        }
    }

    fun updateMove(player: EntityPlayer) {
        player.connection.sendPacket(PacketPlayOutEntityTeleport(this))
    }

    fun destroy(player: EntityPlayer) {
        player.connection.sendPacket(PacketPlayOutEntityDestroy(id))
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

        /** Used to detect if the projectile shot already*/
        var isShooting = false

        fun onTick() = t_()

        override fun shoot(d0: Double, d1: Double, d2: Double, f: Float, f1: Float) {
            super.shoot(d0, d1, d2, f, f1)

            isShooting = true
        }

        override fun a(movingobjectposition: MovingObjectPosition?) {
            val boundingBox = boundingBox.grow(4.0, 4.0, 4.0)

            if (playerName != null) {
                val entities = world.findEntities(EntityPlayer::class, boundingBox)
                for (entity in entities) {
                    if (entity.name != playerName)
                        continue

                    val scene = SceneSystem.individuals[playerName] ?: break

                    if (scene !is IntroScene)
                        break

                    scene.resume()

                    /** Show potion effect particles on the world */
                    scene.player.toNMS().connection.also {
                        it.sendPacket(PacketPlayOutWorldEvent(2002, BlockPosition(this), potionValue, false))
                        it.sendPacket(PacketPlayOutEntityDestroy(id))
                    }
                }
            }

            die()
            return
        }

        /** Methods that deals with the packet layer */


        fun spawnToPlayer(player: EntityPlayer) {
            player.connection.sendPacket(PacketPlayOutSpawnEntity(this, 73, potionValue))
        }

        fun updatePos(player: EntityPlayer, x: Double, y: Double, z: Double) {
            val bX = x.toInt().toByte()
            val bY = y.toInt().toByte()
            val bZ = z.toInt().toByte()
            player.connection.sendPacket(PacketPlayOutEntity.PacketPlayOutRelEntityMove(id, bX, bY, bZ, false))
        }


    }

    init {
        setLocation()

        clearPathfinderSelector(goalSelector)
        clearPathfinderSelector(targetSelector)

        nameHologram.text = "§cPrison Witch"
        spawnHologram()
    }

    fun shoot(target: EntityLiving): Potion {
        val potion = Potion(location.world, this, playerName)
        val targetY = target.locY + target.headHeight.toDouble() - 1.100000023841858
        potion.pitch -= -20F
        val x = target.locX + target.motX - locX
        val y = targetY - locY
        val z = target.locZ + target.motZ - locZ
        val distance = sqrt((x * x) + (z * z))

        potion.shoot(x, y + (distance * 0.2F), z, 0.75F, 0.75F)
        return potion
    }


    /** Methods that deals with the packet layer */


    fun spawnToPlayer(player: EntityPlayer) {
        player.connection.also {
            it.sendPacket(PacketPlayOutSpawnEntityLiving(this))
            val pitch = MathHelper.d(location.pitch * 256.0f / 360.0f).toByte()
            val yaw = MathHelper.d(location.yaw * 256.0f / 360.0f).toByte()
            it.sendPacket(PacketPlayOutEntity.PacketPlayOutEntityLook(id, yaw, pitch, false))
            it.sendPacket(PacketPlayOutEntityHeadRotation(this, yaw))
            it.sendPacket(PacketPlayOutUpdateEntityNBT(id, NBTTagCompound().apply { b(this) }))
        }
    }

    fun updateInventory(player: EntityPlayer) {
        player.connection.also {
            for (slot in 0..4) {
                it.sendPacket(PacketPlayOutEntityEquipment(id, slot, equipment[slot]))
            }
        }
    }

    fun destroy(player: EntityPlayer) {
        player.connection.sendPacket(PacketPlayOutEntityDestroy(id))
    }

}
