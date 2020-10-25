package me.ihdeveloper.humans.core.gui

import me.ihdeveloper.humans.service.api.Profile
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

/**
 * Represents an overview of player's skills
 */
class GUIOverview(
    private val prefix: String,
    private val name: String,
    private val profile: Profile
) : GUIComponent() {

    override fun render(): ItemStack = ItemStack(Material.SKULL_ITEM, 1, 3.toShort()).apply {
        itemMeta = itemMeta.apply {
            if (this is SkullMeta) {
                owner = name
            }

            displayName = "$prefix$name"
            lore = arrayListOf<String>().apply {
                add("§7Overview of yourself")
                add("§0")
                add("§7» §b⚒ Mining Luck Chance§f %${profile.skills.miningLuckChance}")
            }
        }
    }
}
