package me.ihdeveloper.humans.core.util

import kotlin.reflect.KClass
import net.minecraft.server.v1_8_R3.AxisAlignedBB
import net.minecraft.server.v1_8_R3.Entity
import net.minecraft.server.v1_8_R3.MinecraftServer
import net.minecraft.server.v1_8_R3.World
import net.minecraft.server.v1_8_R3.WorldServer
import org.bukkit.Server
import org.bukkit.craftbukkit.v1_8_R3.CraftServer

fun World.toServer(): WorldServer = (this as WorldServer)

fun Server.toNMS(): MinecraftServer = (this as CraftServer).server

fun <T : Entity> World.findEntities(entityClass: KClass<T>, boundingBox: AxisAlignedBB): MutableList<T> = a(entityClass.java, boundingBox)
