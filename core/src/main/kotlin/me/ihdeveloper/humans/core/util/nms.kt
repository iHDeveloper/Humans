package me.ihdeveloper.humans.core.util

import kotlin.reflect.KClass
import net.minecraft.server.v1_8_R3.AxisAlignedBB
import net.minecraft.server.v1_8_R3.Entity
import net.minecraft.server.v1_8_R3.World
import net.minecraft.server.v1_8_R3.WorldServer

fun World.toServer(): WorldServer = (this as WorldServer)

fun <T : Entity> World.findEntities(entityClass: KClass<T>, boundingBox: AxisAlignedBB) = a(entityClass.java, boundingBox)
