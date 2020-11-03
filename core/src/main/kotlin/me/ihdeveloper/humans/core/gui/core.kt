package me.ihdeveloper.humans.core.gui

import me.ihdeveloper.humans.core.GameItemStack
import me.ihdeveloper.humans.core.registry.getItemInfo
import me.ihdeveloper.humans.core.registry.getItemInstance
import me.ihdeveloper.humans.core.util.addGameItem
import me.ihdeveloper.humans.core.util.hasGameItem
import me.ihdeveloper.humans.core.util.itemMeta
import me.ihdeveloper.humans.core.util.removeGameItem
import me.ihdeveloper.humans.service.api.Profile
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
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

/**
 * Represents a blank field in the screen
 */
class GUIBlank : GUIComponent() {
    override fun render(): ItemStack = ItemStack(Material.STAINED_GLASS_PANE, 1, 15.toShort()).apply {
        itemMeta {
            displayName = "§0⠀"
            addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
        }
    }
}

/**
 * Represents a shop
 */
class GUIShopSale(
    private val item: GameItemStack,
    private val cost: GameItemStack,
) : GUIComponent(), GUIRenderByPlayer, GUIOnClick {

    override fun render(): ItemStack {
        TODO("Not yet implemented")
    }

    override fun renderByPlayer(player: Player): ItemStack {
        val itemInfo = getItemInfo(item.type)
        val costInfo = getItemInfo(cost.type)

        if (itemInfo === null || costInfo === null) {
            return ItemStack(Material.BARRIER).apply {
                itemMeta {
                    displayName = "§7Unknown"
                    lore = arrayListOf(
                        "§7This is unknown item",
                        "§7Due to the item being removed in the game",
                        "§7",
                        "§cYOU CAN'T PURCHASE THIS ITEM",
                    )
                }
            }
        }

        return ItemStack(itemInfo.material, item.amount, itemInfo.data).apply {
            itemMeta {
                displayName = "${itemInfo.rarity.color}${itemInfo.name}"
                lore = arrayListOf(*itemInfo.description).apply {
                    addAll(arrayOf(
                        "§7",
                        "§eCosts",
                        "§8» §7x${cost.amount}${costInfo.rarity.color} ${costInfo.name}",
                        "§7",
                        if (player.inventory.hasGameItem(cost)) "§aYou can purchase this item!" else "§cYou can't purchase this item!",
                        "§7",
                        "§8-----------------",
                        "${itemInfo.rarity.color}${ChatColor.BOLD}${itemInfo.rarity.name} ${getItemInstance(item.type)?.raritySuffix ?: ""}",
                    ))
                }
                addItemFlags(*itemInfo.flags)
            }
        }
    }

    override fun onClick(player: Player): Boolean {
        player.run {
            if (inventory.removeGameItem(cost)) {
                inventory.addGameItem(item)
                sendMessage("§aYou purchased the item $cost§a!")
            } else {
                sendMessage("§cFailed to purchase the item $cost§c!")
            }
        }
        return true
    }
}
