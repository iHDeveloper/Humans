package me.ihdeveloper.humans.core.entity

import com.mojang.authlib.GameProfile
import me.ihdeveloper.humans.core.util.GameLogger
import me.ihdeveloper.humans.core.setPrivateField
import me.ihdeveloper.humans.core.registry.spawnEntity
import me.ihdeveloper.humans.core.system.NPCSystem
import me.ihdeveloper.humans.core.system.TEAM_NPC
import me.ihdeveloper.humans.core.util.toNMSWorld
import net.minecraft.server.v1_8_R3.DamageSource
import net.minecraft.server.v1_8_R3.Entity
import net.minecraft.server.v1_8_R3.EntityArmorStand
import net.minecraft.server.v1_8_R3.EntityGiantZombie
import net.minecraft.server.v1_8_R3.EntityMinecartRideable
import net.minecraft.server.v1_8_R3.EntityPlayer
import net.minecraft.server.v1_8_R3.EntitySkeleton
import net.minecraft.server.v1_8_R3.EnumProtocolDirection
import net.minecraft.server.v1_8_R3.MathHelper
import net.minecraft.server.v1_8_R3.NetworkManager
import net.minecraft.server.v1_8_R3.PacketPlayOutAnimation
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityHeadRotation
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo
import net.minecraft.server.v1_8_R3.PathfinderGoalSelector
import net.minecraft.server.v1_8_R3.PlayerConnection
import net.minecraft.server.v1_8_R3.PlayerInteractManager
import net.minecraft.server.v1_8_R3.WorldServer
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_8_R3.CraftServer
import org.bukkit.craftbukkit.v1_8_R3.util.UnsafeList
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractAtEntityEvent

private const val NPC_RENDER_DISTANCE = 48

/**
 * An custom entity for armor stand
 */
open class CustomArmorStand(location: Location)
    : EntityArmorStand(toNMSWorld(location.world), location.x, location.y, location.z) {

    /**
     * Fix: Gravity flag value is reversed
     */
    override fun setGravity(flag: Boolean) {
        super.setGravity(!flag)
    }

}

open class CustomSkeleton(
    private val location: Location
) : EntitySkeleton(toNMSWorld(location.world)) {

    protected val nameHologram = Hologram(location.clone().apply { y += 1 }, "§cSkeleton")

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
    var holograms: Array<Hologram>? = null

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
    fun shouldTrack(player: Player) = location.distance(player.location) <= NPC_RENDER_DISTANCE

    /**
     * Spawns the NPC to the player. And, adds it to the tracking set
     */
    fun spawn(player: EntityPlayer) {
        player.bukkitEntity.scoreboard.apply {
            getTeam(TEAM_NPC).addEntry(profile.name)
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

}
