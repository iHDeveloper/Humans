package me.ihdeveloper.humans.core.entity

import me.ihdeveloper.humans.core.core
import me.ihdeveloper.humans.core.entity.npc.PrisonerType
import me.ihdeveloper.humans.core.entity.npc.newPrisoner
import net.minecraft.server.v1_8_R3.Entity
import net.minecraft.server.v1_8_R3.EntityPlayer
import net.minecraft.server.v1_8_R3.PlayerConnection
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack

/**
 * Generates an entity class depending on the type
 */
fun fromEntityType(type: String, location: Location): Entity? = when(type) {
    /** Prison entities */
    "prison_guard" -> PrisonGuard(location)
    "prison_watcher" -> PrisonWatcher(location)
    "prison_witch" -> PrisonWitch(location)

    /** Hologram entities */
    "hologram" -> Hologram(location, "Text")
    "pickaxe_hologram" -> ItemHologram(location, ItemStack(Material.STONE_PICKAXE).apply {
        addEnchantment(Enchantment.DIG_SPEED, 1)
    })
    else -> core.integratedPart?.fromEntityType(type, location)
}

/**
 * Generates an NPC depending on the given type
 */
fun fromNPCType(type: String, location: Location): CustomNPC? = when(type) {
    /** General NPCs */
    "hub_selector" -> HubSelector(location)
    "agent_developer" -> AgentDeveloper(location)

    /** Prisoners */
    "prisoner_sattam" -> newPrisoner(location, PrisonerType.CAMERON)
    "prisoner_dhoom" -> newPrisoner(location, PrisonerType.HAKIM)
    "prisoner_almond" -> newPrisoner(location, PrisonerType.SARAH)
    "prisoner_brhom" -> newPrisoner(location, PrisonerType.XANDER)
    "prisoner_ali" -> newPrisoner(location, PrisonerType.ALI)
    else -> core.integratedPart?.fromNPCType(type, location)
}

var EntityPlayer.connection: PlayerConnection
    get() = playerConnection
    set(value) { playerConnection = value }
