package me.ihdeveloper.humans.core.entity

import com.mojang.authlib.GameProfile
import me.ihdeveloper.humans.core.entity.event.EntityOnClick
import me.ihdeveloper.humans.core.getPrivateField
import me.ihdeveloper.humans.core.registry.spawnEntity
import me.ihdeveloper.humans.core.setPrivateField
import me.ihdeveloper.humans.core.system.NPCSystem
import me.ihdeveloper.humans.core.system.TEAM_NPC
import me.ihdeveloper.humans.core.util.GameLogger
import me.ihdeveloper.humans.core.util.NMSItemStack
import me.ihdeveloper.humans.core.util.broadcastPacket
import me.ihdeveloper.humans.core.util.toNMS
import me.ihdeveloper.humans.core.util.toNMSWorld
import me.ihdeveloper.humans.core.util.toServer
import net.minecraft.server.v1_8_R3.Block
import net.minecraft.server.v1_8_R3.DamageSource
import net.minecraft.server.v1_8_R3.Entity
import net.minecraft.server.v1_8_R3.EntityArmorStand
import net.minecraft.server.v1_8_R3.EntityFallingBlock
import net.minecraft.server.v1_8_R3.EntityGiantZombie
import net.minecraft.server.v1_8_R3.EntityHuman
import net.minecraft.server.v1_8_R3.EntityLightning
import net.minecraft.server.v1_8_R3.EntityLiving
import net.minecraft.server.v1_8_R3.EntityMinecartRideable
import net.minecraft.server.v1_8_R3.EntityPlayer
import net.minecraft.server.v1_8_R3.EntityPotion
import net.minecraft.server.v1_8_R3.EntitySkeleton
import net.minecraft.server.v1_8_R3.EntityWitch
import net.minecraft.server.v1_8_R3.EnumProtocolDirection
import net.minecraft.server.v1_8_R3.MathHelper
import net.minecraft.server.v1_8_R3.NetworkManager
import net.minecraft.server.v1_8_R3.PacketPlayOutAnimation
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityEquipment
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityHeadRotation
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityWeather
import net.minecraft.server.v1_8_R3.PathfinderGoalSelector
import net.minecraft.server.v1_8_R3.PlayerConnection
import net.minecraft.server.v1_8_R3.PlayerInteractManager
import net.minecraft.server.v1_8_R3.Vec3D
import net.minecraft.server.v1_8_R3.WorldServer
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.craftbukkit.v1_8_R3.CraftServer
import org.bukkit.craftbukkit.v1_8_R3.util.UnsafeList
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.scoreboard.NameTagVisibility

private const val NPC_RENDER_DISTANCE = 48

/**
 * An custom entity for armor stand
 */
open class CustomArmorStand(protected val location: Location)
    : EntityArmorStand(toNMSWorld(location.world), location.x, location.y, location.z) {

    /**
     * Updates the location of the entity in the world
     *
     * Note: It should be used during the initialization of the final class
     */
    fun setLocation() {
        location.run { setLocation(x, y, z, yaw, pitch) }

        /** Change the head rotation of the entity */
        super.aK = location.yaw
    }

    /**
     * Fix: Gravity flag value is reversed
     */
    override fun setGravity(flag: Boolean) {
        super.setGravity(!flag)
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
        if (this is EntityOnClick) {
            if (damagesource!!.entity is EntityPlayer) {
                return super.damageEntity(damagesource, f)
            }
        }
        return false
    }
}

/**
 * Custom entity for skeleton
 */
open class CustomSkeleton(
    private val location: Location
) : EntitySkeleton(toNMSWorld(location.world)) {
    protected val nameHologram = Hologram(location.clone().apply { y += 1 }, "§cSkeleton")
    protected var frozen = true

    /**
     * Updates the location of the entity in the world
     *
     * Note: It should be used during the initialization of the final class
     */
    fun setLocation() {
        setLocation(location.x, location.y, location.z, location.yaw, location.pitch)

        /** Change the head rotation of the entity */
        super.aK = location.yaw
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

    /**
     * Prevent the skeleton from burning by fire
     */
    override fun m() {
        return
    }

    /**
     * Prevent the skeleton from moving
     */
    override fun move(d0: Double, d1: Double, d2: Double) {
        if (frozen)
            return
        super.move(d0, d1, d2)
    }

    /**
     * Prevent the skeleton from colliding with anyone
     */
    override fun collide(entity: Entity?) {
        return
    }

    /**
     * Prevent the skeleton from getting damaged
     */
    override fun damageEntity(damagesource: DamageSource?, f: Float): Boolean {
        return false
    }

    /**
     * Prevent the skeleton from moving
     */
    override fun g(d0: Double, d1: Double, d2: Double) {
        return
    }

    /**
     * Prevent the skeleton from attacking anyone. If a bow is in the skeleton's hand
     */
    override fun n() {
        return
    }

}

/**
 * Custom entity for rideable mine cart
 */
open class CustomMineCart(location: Location)
    : EntityMinecartRideable(toNMSWorld(location.world))

/**
 * Custom entity for non-player character
 */
open class CustomNPC(
    val location: Location,
    profile: GameProfile
) : EntityPlayer(
    (Bukkit.getServer() as CraftServer).server,
    toNMSWorld(location.world) as WorldServer,
    profile,
    PlayerInteractManager(toNMSWorld(location.world))
) {
    val trackedPlayers = mutableSetOf<Int>()
    private var holograms: Array<Hologram>? = null

    fun initNPC() {
        connection = PlayerConnection(server, NetworkManager(EnumProtocolDirection.SERVERBOUND), this)

        customNameVisible = false

        location.apply { setLocation(x, y, z, yaw, pitch) }

        /** Enable all skin parts in the NPC */
        datawatcher.watch(10, 255.toByte())

        world.addEntity(this)
    }

    fun initHologram(name: String, type: String, state: String) {
        holograms = spawnNPCHologram(location, name, type, state, GameLogger("Core/NPC/Logger"))
    }

    /**
     * Check if the NPC should track this player or not
     */
    fun shouldTrack(location: Location) = (this.location.distance(location) <= NPC_RENDER_DISTANCE)

    /**
     * Spawns the NPC to the player. And, adds it to the tracking set
     */
    fun spawn(player: EntityPlayer) {
        player.bukkitEntity.scoreboard.apply {
            (getTeam(TEAM_NPC) ?: registerNewTeam(TEAM_NPC).apply {
                prefix = "§8[NPC] "
                nameTagVisibility = NameTagVisibility.HIDE_FOR_OTHER_TEAMS
            }).addEntry(profile.name)
        }

        trackedPlayers.add(player.id)

        val headYaw = MathHelper.d(headRotation * 256.0f / 360.0f)

        player.connection.run {
            sendPacket(PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, this@CustomNPC))
            sendPacket(PacketPlayOutNamedEntitySpawn(this@CustomNPC))
            sendPacket(PacketPlayOutEntityHeadRotation(this@CustomNPC, headYaw.toByte()))

            NPCSystem.scheduleRemovePacket(this, this@CustomNPC)
        }

        update(player)
    }

    /**
     * Despawns the NPC from the player. And remove it from the tracking set
     */
    fun despawn(player: EntityPlayer) {
        trackedPlayers.remove(player.id)
    }

    override fun die() {
        trackedPlayers.clear()
        holograms?.forEach { it.die() }
    }

    override fun getHeadRotation() = location.yaw

    /**
     * Invoked when [PlayerInteractAtEntityEvent] is fired
     */
    open fun interact(player: Player) {}

    /**
     * Updates the state of the NPC to the player
     */
    private fun update(player: EntityPlayer) {
        player.connection.run {

            /** Send an animation of the NPC's arm swinging */
            sendPacket(PacketPlayOutAnimation(this@CustomNPC, 0))
            sendPacket(PacketPlayOutEntityEquipment(id, 4, getEquipment(4)))
        }
    }
}

/**
 * Custom entity for giant zombie
 */
open class CustomGiant(
    private val location: Location
) : EntityGiantZombie(toNMSWorld(location.world)) {

    protected fun initLocation() {
        location.run { setLocation(x, y, z, yaw, pitch) }

        /** Change the head rotation of the entity */
        super.aK = location.yaw
    }

    /**
     * Prevent the giant from burning by fire
     */
    override fun m() {
        return
    }

    /**
     * Prevent the giant from moving
     */
    override fun move(d0: Double, d1: Double, d2: Double) {
        return
    }

    /**
     * Prevent the giant from colliding with anyone
     */
    override fun collide(entity: Entity?) {
        return
    }

    /**
     * Prevent the giant from getting damaged
     */
    override fun damageEntity(damagesource: DamageSource?, f: Float): Boolean {
        return false
    }

    /**
     * Prevent the giant from moving
     */
    override fun g(d0: Double, d1: Double, d2: Double) {
        return
    }

}

/**
 * Custom entity for witch
 */
open class CustomWitch(
    private val location: Location
) : EntityWitch(toNMSWorld(location.world)) {

    protected val nameHologram = Hologram(location.clone().apply { y += 1.25 }, "§cWitch")

    protected fun setLocation() {
        location.run {
            setLocation(x, y, z, yaw, pitch)
            super.aK = yaw
        }
    }

    /**
     * Clears [PathfinderGoalSelector] using Reflection
     */
    protected fun clearPathfinderSelector(selector: PathfinderGoalSelector) {
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

    /**
     * Override witch's tick function with nothing
     */
    override fun m() {
        return
    }

    /**
     * Prevent the witch from moving
     */
    override fun move(d0: Double, d1: Double, d2: Double) {
        return
    }

    /**
     * Prevent the witch from colliding with anyone
     */
    override fun collide(entity: Entity?) {
        return
    }

    /**
     * Prevent the witch from getting damaged
     */
    override fun damageEntity(damagesource: DamageSource?, f: Float): Boolean {
        return false
    }

    /**
     * Prevent the witch from moving
     */
    override fun g(d0: Double, d1: Double, d2: Double) {
        return
    }

}

/**
 * Custom entity for potions
 */
open class CustomPotion(
    world: World,
    entity: EntityLiving,
    type: Int
) : EntityPotion(toNMSWorld(world), entity, type)

/**
 * Represents a lightning entity on location
 */
open class CustomLightning(
    location: Location,
    isEffect: Boolean,
) : EntityLightning(toNMSWorld(location.world), location.x, location.y, location.z, isEffect) {

    override fun t_() {
        when (getPrivateField<Int>(EntityLightning::class, this, "lifeTicks")) {
            2 -> world.broadcastPacket(locX, locY, locZ, 512.0, PacketPlayOutSpawnEntityWeather(this))
            0 -> setPrivateField(EntityLightning::class, this, "c", 0)
        }

        super.t_()
    }

}

@Suppress("DEPRECATION")
open class CustomFallingBlock(
    type: Material,
    location: Location
) : EntityFallingBlock(toNMSWorld(location.world), location.x, location.y, location.z, Block.getById(type.id).blockData)
