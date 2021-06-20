package me.ihdeveloper.humans.core.entity

import kotlin.math.sqrt
import me.ihdeveloper.humans.core.CustomStatefulEntity
import me.ihdeveloper.humans.core.corePlugin
import me.ihdeveloper.humans.core.registry.spawnEntity
import me.ihdeveloper.humans.core.scene.IntroScene
import me.ihdeveloper.humans.core.system.SceneSystem
import me.ihdeveloper.humans.core.util.NMSItemStack
import me.ihdeveloper.humans.core.util.applyTexture
import me.ihdeveloper.humans.core.util.broadcastPacket
import me.ihdeveloper.humans.core.util.compress
import me.ihdeveloper.humans.core.util.findEntities
import me.ihdeveloper.humans.core.util.gameProfile
import me.ihdeveloper.humans.core.util.randomGameProfile
import me.ihdeveloper.humans.core.util.sequenceLimit
import me.ihdeveloper.humans.core.util.toAngle
import me.ihdeveloper.humans.core.util.toNMS
import me.ihdeveloper.spigot.devtools.api.DevTools
import net.minecraft.server.v1_8_R3.BlockPosition
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
import net.minecraft.server.v1_8_R3.Vector3f
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.ArmorStand
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.scheduler.BukkitTask
import org.bukkit.util.EulerAngle

private const val GUARD_MAX_STEPS = 60
private const val GUARD_SPEED = 0.25
private const val GUARD_ROTATE_SPEED = 1.25F
private const val GUARD_TRACK_DISTANCE = 48.0
private const val GUARD_STATUS_NONE = "§9"
private const val GUARD_STATUS_LOOKING = "§eWatching!"
private const val GUARD_STATUS_LOOKING_REVERSE = "§4§lAlways watching!"

private const val WATCHER_Y_RANGE = 0.5
private const val WATCHER_Y_SPEED = 0.1

/**
 * A guard that protects the prison
 *
 * ~~Currently it has no action to do. Instead of being a "guard"~~ (not anymore lol)
 */
class PrisonGuard(
    location: Location,
    private var move: Boolean,
    private var direction: Boolean = false, /* 0 = x, 1 = z */
) : CustomSkeleton(location), CustomStatefulEntity {
    private val statusHologram = Hologram(location.clone().apply { y += 1.25 }, GUARD_STATUS_NONE)
    private var steps = 0
    private var reverse = false
    private var needToRotate = false
    private var fakeYaw = 0F
    private var targetYaw = 0F

    init {
        frozen = false
        setLocation()

        clearPathfinderSelector(goalSelector)
        clearPathfinderSelector(targetSelector)

        setEquipment(0, NMSItemStack(Items.IRON_SWORD))
        setEquipment(2, NMSItemStack(Items.LEATHER_CHESTPLATE))
        setEquipment(3, NMSItemStack(Items.LEATHER_LEGGINGS))

        nameHologram.text = "§cPrison Guard"
        spawnHologram()

        statusHologram.text = ""
        statusHologram.customNameVisible = false
        spawnEntity(statusHologram, false)
    }

    override fun t_() {
        super.t_()

        if (move) {
            think()
            walk()
        }
    }

    override fun load(state: Map<String, Any>) {
        move = state["move"] as Boolean
        direction = state["direction"] as Boolean
    }

    override fun store(state: MutableMap<String, Any>) {
        state["move"] = move
        state["direction"] = direction
    }

    private fun think() {
        if (needToRotate) return

        if (steps >= GUARD_MAX_STEPS) {
            steps = 0
            reverse = !reverse
            needToRotate = true
            fakeYaw = yaw + 180
            targetYaw = fakeYaw + 180
        }

        var targetX = locX.toInt()
        var targetZ = locZ.toInt()
        if (!direction && !reverse) {
            targetX += 3
        } else if (!direction && reverse) {
            targetX -= 3
        } else if (direction && !reverse) {
            targetZ += 3
        } else if (direction && reverse) {
            targetZ -= 3
        }

        val topBlock = super.world.world.getBlockAt(targetX, locY.toInt() + 1, targetZ).type.isSolid
        val centerBlock = super.world.world.getBlockAt(targetX, locY.toInt(), targetZ).type.isSolid
        val bottomBlock = super.world.world.getBlockAt(targetX, locY.toInt() - 1, targetZ).type.isSolid

        if (topBlock || centerBlock || !bottomBlock) {
            steps = 0
            reverse = !reverse
            needToRotate = true
            fakeYaw = yaw + 180
            targetYaw = fakeYaw + 180
        }
    }

    private fun walk() {
        if (needToRotate) {
            if (fakeYaw >= targetYaw) {
                needToRotate = false
                statusHologram.customNameVisible = false
            } else {
                statusHologram.customNameVisible = true
                statusHologram.text = if (reverse) GUARD_STATUS_LOOKING else GUARD_STATUS_LOOKING_REVERSE

                fakeYaw += GUARD_ROTATE_SPEED
                yaw += GUARD_ROTATE_SPEED
                yaw = ((yaw + 180F) sequenceLimit 360F) - 180F
                val packet = PacketPlayOutEntityHeadRotation(this, yaw.toAngle().compress())
                world.broadcastPacket(locX, locY, locZ, GUARD_TRACK_DISTANCE, packet)
                return
            }
        }

        steps++

        var dest = GUARD_SPEED
        if (reverse) dest *= -1

        if (!direction) {
            super.move(dest, 0.0, 0.0)
            nameHologram.move(dest, 0.0, 0.0)
            statusHologram.move(dest, 0.0, 0.0)
        } else {
            super.move(0.0, 0.0, dest)
            nameHologram.move(0.0, 0.0, dest)
            statusHologram.move(0.0, 0.0, dest)
        }
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

        /** Used to detect if the projectile shot already -*/
        private var isShooting = false

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

class PrisonCamera(
    location: Location
) : CustomArmorStand(location) {
    companion object {
        private const val TEXTURE_DATA = "ewogICJ0aW1lc3RhbXAiIDogMTYyNDAwOTEyNzUwNywKICAicHJvZmlsZUlkIiA6ICIyM2YxYTU5ZjQ2OWI0M2RkYmRiNTM3YmZlYzEwNDcxZiIsCiAgInByb2ZpbGVOYW1lIiA6ICIyODA3IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzQ3OTliYTIyN2YxZjI1YjI4N2Y3ZDc4MTRlNTI2NGRjZTJjZDY5OWU1ZDFlYmY1NjJmNWVlZDkwYjA1ODE5YTgiCiAgICB9CiAgfQp9"
        private const val TEXTURE_SIGNATURE = "wBF3HdA/snoQ8HJGBNH6g1UYXBYPC7ziBbV/u7TUaqjfoD4WXZW6lVB1LZTEwLOqE0WLiYGduk5t8fr0yBKvDNz2RrHwO9Tayx+DoNB8NlufK3RkacfvA7Xzq4eLJTm8sw599s7/fddMHUr5QzWm7qJ/LpMyIFF1RzelHSqyGpabpXaD1nuz4mKe8DQzUw7DDqzuQyLRTfB6zXn39QjUb8Axahl7BD18RKD3bQgDYUOGRKF00aH/LTnoV0w2hYzLKSTxtbEUEI8cSAUYbjPHyUUsyVtXJ/jvsfH0xk070CWwCCzk7MPkypdqN3eapIQySxsCU80VzedCb/8JlWkdNhQRnMtQNaN/+MVv7xaLzVzQKhju0/1JMLdLaEXMx1lKNBpFw/FQ06aKf3ZO1hgMtAwhbV4FztCoQcRgbQGmLnbcZ6G/+gI7IyXl4FodOLz2YVnxIkCFzL8DCeayEyjXlRamFJgJJ+J03Ez+gBrJkfmXton7VXX8jpqO49oiSY0FGfX36LYwCj+pSuZttPA39jzUGv03F1X8GekJpQEL5oCQ8rZUmU/dg532ONuSykgTH5JEPsCi136KMcjQw0oa7vPgHIvHmel5jptL1xotnCT7aGjEknv2sTpoC2WqAlR9CP80EulEE2kLSv+tFwuU7/3eYNuUABx7PwLJCAahGPg="
    }

    init {
        isInvisible = true
        setGravity(false)
        setLocation()

        val textureItem = ItemStack(Material.SKULL_ITEM, 1, 3.toShort()).apply {
            itemMeta = (itemMeta as SkullMeta).apply {
                gameProfile = randomGameProfile().apply {
                    applyTexture(TEXTURE_DATA, TEXTURE_SIGNATURE)
                }
            }
        }

        (getBukkitEntity() as ArmorStand).apply {
            equipment.helmet = textureItem

            headPose = EulerAngle(0.0, 0.0, 0.0)
        }
    }

    private var angle = 0f
    private var reverse = false
    override fun t_() {
        val rotationSpeed = 0.75f
        if (reverse) {
            angle -= rotationSpeed
        } else {
            angle += rotationSpeed
        }

        if (!reverse && angle >= 45f) {
            angle = 45f
            reverse = true
        } else if (reverse && angle <= -45f) {
            angle = -45f
            reverse = false
        }
        setHeadPose(Vector3f(180f, angle, 0f))

        super.t_()
    }
}
