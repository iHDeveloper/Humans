package me.ihdeveloper.humans.core.entity

import com.mojang.authlib.GameProfile
import me.ihdeveloper.humans.core.GameLogger
import me.ihdeveloper.humans.core.setPrivateField
import me.ihdeveloper.humans.core.spawnEntity
import me.ihdeveloper.humans.core.system.TEAM_NPC
import me.ihdeveloper.humans.core.toNMSWorld
import net.minecraft.server.v1_8_R3.EntityArmorStand
import net.minecraft.server.v1_8_R3.EntityMinecartRideable
import net.minecraft.server.v1_8_R3.EntityPlayer
import net.minecraft.server.v1_8_R3.EntitySkeleton
import net.minecraft.server.v1_8_R3.EnumProtocolDirection
import net.minecraft.server.v1_8_R3.NetworkManager
import net.minecraft.server.v1_8_R3.PacketPlayOutAnimation
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
     * Spawns the NPC to the player. And, adds it to the tracking set
     */
    fun spawn(player: EntityPlayer) {
        player.bukkitEntity.scoreboard.apply {
            getTeam(TEAM_NPC).addEntry(profile.name)
        }

        trackedPlayers.add(player.id)

        player.connection.run {
            sendPacket(PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, this@CustomNPC))
            sendPacket(PacketPlayOutNamedEntitySpawn(this@CustomNPC))
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