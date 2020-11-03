package me.ihdeveloper.humans.core.item

import me.ihdeveloper.humans.core.GameItemInfo
import me.ihdeveloper.humans.core.GameItemRarity
import org.bukkit.Material

/** Prison Mine Passes */

@GameItemInfo(
    id = "prison:pass:coal",
    name = "Prison Coal Pass",
    description = [
        "§7Gives you access to the §eCoal Mine",
        "§7",
        "§8» §7Go to §eCoal Mine",
        "§8» §7You should give this to §cPrison Mine Wizard",
        "§7",
        "§cOne pass of the same type per player"
    ],
    rarity = GameItemRarity.RARE,
    material = Material.EMPTY_MAP,
)
class PrisonCoalPass : MinePassItem()

