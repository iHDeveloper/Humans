package me.ihdeveloper.humans.core.entity

import net.minecraft.server.v1_8_R3.Entity
import net.minecraft.server.v1_8_R3.EntityPlayer
import net.minecraft.server.v1_8_R3.ItemStack
import net.minecraft.server.v1_8_R3.Items
import net.minecraft.server.v1_8_R3.PlayerConnection
import org.bukkit.Location

/**
 * Generates an entity class depending on the type
 */
fun fromEntityType(type: String, location: Location): Entity? = when(type) {
    "prison_guard" -> PrisonGuard(location)
    "hologram" -> Hologram(location, "Text")
    "pickaxe_hologram" -> ItemHologram(location, ItemStack(Items.STONE_PICKAXE))
    else -> null
}

/**
 * Generates an NPC depending on the given type
 */
fun fromNPCType(type: String, location: Location): CustomNPC? = when(type) {
    "hub_selector" -> HubSelector(location)
    "agent_developer" -> AgentDeveloper(location)
    else -> null
}

var EntityPlayer.connection: PlayerConnection
    get() = playerConnection
    set(value) { playerConnection = value }
