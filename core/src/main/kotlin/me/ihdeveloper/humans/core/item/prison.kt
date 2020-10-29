package me.ihdeveloper.humans.core.item

import me.ihdeveloper.humans.core.GameItemInfo
import me.ihdeveloper.humans.core.GameItemPickaxe
import me.ihdeveloper.humans.core.GameItemRarity
import org.bukkit.Material
import org.bukkit.inventory.ItemFlag

/** Natural Items */

@GameItemInfo(
    id = "prison:stone",
    name = "Prison Stone",
    description = [],
    rarity = GameItemRarity.COMMON,
    material = Material.STONE
)
class PrisonStone : NaturalItem()

@GameItemInfo(
    id = "prison:pickaxe:cursed",
    name = "Prison Cursed Pickaxe",
    description = [
        "§7Crafted in the §c§l☠§c Humans Slaughter",
        "§7Cursed by §cPrison Witch",
        "§7",
        "§c§l☣§c Item Curse",
        "§8» §7Each time a block mined by this pickaxe",
        "§8» §7You get §cMiner Fatigue§7 for §c5 seconds", // TODO Design a system for cursed items
    ],
    rarity = GameItemRarity.UNCOMMON,
    material = Material.WOOD_PICKAXE,
    flags = [ItemFlag.HIDE_ATTRIBUTES],
    unbreakable = true
)
@GameItemPickaxe
class PrisonCursedPickaxe : ToolItem()
