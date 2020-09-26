package me.ihdeveloper.humans.core.entity

import me.ihdeveloper.humans.core.toMinecraftWorld
import net.minecraft.server.v1_8_R3.EntityArmorStand
import org.bukkit.Location

/**
 * An custom entity for armor stand
 */
open class CustomArmorStand(location: Location)
    : EntityArmorStand(toMinecraftWorld(location.world), location.x, location.y, location.z)
