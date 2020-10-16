package me.ihdeveloper.humans.core.util

import net.minecraft.server.v1_8_R3.World
import net.minecraft.server.v1_8_R3.WorldServer

fun World.toServer(): WorldServer = (this as WorldServer)
