package me.ihdeveloper.humans.core.item

import me.ihdeveloper.humans.core.GameItemInfo
import me.ihdeveloper.humans.core.GameItemRarity
import org.bukkit.Material

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
    id = "prison:pickaxe",
    name = "Prison Pickaxe",
    description = ["§7Made in Wither Prison."],
    rarity = GameItemRarity.UNCOMMON,
    material = Material.WOOD_PICKAXE
)
class PrisonPickaxe : ToolItem()
