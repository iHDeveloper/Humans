package me.ihdeveloper.humans.core.util

import com.mojang.authlib.GameProfile
import kotlin.reflect.KClass
import net.minecraft.server.v1_8_R3.AxisAlignedBB
import net.minecraft.server.v1_8_R3.Entity
import net.minecraft.server.v1_8_R3.EntityHuman
import net.minecraft.server.v1_8_R3.EntityPlayer
import net.minecraft.server.v1_8_R3.MinecraftServer
import net.minecraft.server.v1_8_R3.Packet
import net.minecraft.server.v1_8_R3.PacketListenerPlayOut
import net.minecraft.server.v1_8_R3.World
import net.minecraft.server.v1_8_R3.WorldServer
import org.bukkit.Server
import org.bukkit.craftbukkit.v1_8_R3.CraftServer
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Player

fun World.toServer(): WorldServer = (this as WorldServer)

fun Server.toNMS(): MinecraftServer = (this as CraftServer).server

fun <T : Entity> World.findEntities(entityClass: KClass<T>, boundingBox: AxisAlignedBB): MutableList<T> = a(entityClass.java, boundingBox)

fun World.broadcastPacket(x: Double, y: Double, z: Double, distance: Double, packet: Packet<PacketListenerPlayOut>) {
    server.toNMS().playerList.sendPacketNearby(x, y, z, distance, toServer().dimension, packet)
}

/**
 * Converts [Player] to [EntityPlayer]
 */
fun Player.toNMS(): EntityPlayer = (this as CraftPlayer).handle

private val field_GameProfile = EntityHuman::class.java.getDeclaredField("bH").apply {
    isAccessible = true
}
val EntityPlayer.gameProfile: GameProfile
    get() {
        return field_GameProfile.get(this) as GameProfile
    }
