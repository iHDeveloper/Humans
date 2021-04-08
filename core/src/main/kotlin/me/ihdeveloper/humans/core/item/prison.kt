package me.ihdeveloper.humans.core.item

import com.mojang.authlib.GameProfile
import me.ihdeveloper.humans.core.GameItem
import me.ihdeveloper.humans.core.GameItemInfo
import me.ihdeveloper.humans.core.GameItemOnBreak
import me.ihdeveloper.humans.core.GameItemPickaxe
import me.ihdeveloper.humans.core.GameItemRarity
import me.ihdeveloper.humans.core.GameItemTexture
import me.ihdeveloper.humans.core.GameItemWithTexture
import me.ihdeveloper.humans.core.util.sendActionBar
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

/** Natural Items */

@GameItemInfo(
    id = "prison:stone",
    name = "Prison Stone",
    description = [],
    rarity = GameItemRarity.COMMON,
    material = Material.STONE,

    stackable = true,
)
class PrisonStone : NaturalItem()

@GameItemInfo(
    id = "prison:enchanted:stone",
    name = "Prison Enchanted Stone",
    description = ["§7An enchanted form of §fStone"],
    rarity = GameItemRarity.RARE,
    material = Material.STONE,

    stackable = true,
)
class PrisonEnchantedStone : EnchantedNaturalItem()

/** Prison Mine Items */

@GameItemInfo(
    id = "prison:crystal",
    name = "Prison Crystal",
    description = [
        "§7Provides the §cPrison Wizard§7 the power",
        "§7to spawn §eEnchanted§7 form of blocks",
        "§7",
        "§7Placeable in any prison mine",
        "§8» §7Place it on §cPrison Wizard Table"
    ],
    rarity = GameItemRarity.EPIC,
    material = Material.SKULL_ITEM,
    data = 3,

    stackable = false,
)
@GameItemTexture(
    texture = "ewogICJ0aW1lc3RhbXAiIDogMTYwMzYxMDQ0MzU4MywKICAicHJvZmlsZUlkIiA6ICIzM2ViZDMyYmIzMzk0YWQ5YWM2NzBjOTZjNTQ5YmE3ZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJEYW5ub0JhbmFubm9YRCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9lNDllYzdkODJiMTQxNWFjYWUyMDU5Zjc4Y2QxZDE3NTRiOWRlOWIxOGNhNTlmNjA5MDI0YzRhZjg0M2Q0ZDI0IgogICAgfQogIH0KfQ==",
    signature = "Mnf7PDLe+FPiO+wQ2St6XNRiiIXtZ3GuPTcLlM7pNQ6d6MXuzI7xXG24qaAMFuVwMB+F3dLYcaFlc+bWyi3Qm9msSq2mMUXdvzTamAslZHcdcTFNpppkYgdvkOhWK7W/amQyd2Q+pLDECe8Mg6gxBY17+xfaWlIynzEWEmHR+ye+hTC44kgiTZaYiRg7gpU002deY8WpX875cc5zJIroxVR52qHIV+suIMPwq47mpCp520J9R1HuYvvP/V3+PwL7skMlC1F/HHkG5A13fvSKMqq9XMsdqXR8qvWlcL5IQTS7ijtD9TZo8jcmhz/7HCXuJ912I1GqJp4hZ0Lqa0NB0TuI/giHr2i4yNzORe6oan47bpMXLoZWIrYZIOsF6wSObhwniF1jM/zUEkum9XswRImIvYYlmyLH+Kkh5uQJm244rOLPXmOZEid6PW5bhaSRpMOMpxboeOtjLbGC56Ev+DwoI37SrAYY6/LC7HwjVhvkcsLd/9BrF+Wl10bdLdsJEbd+TII59/45MM1x7+xgeAFU/ip0TjkMPfRLdNmfxOGssMFZOaM55iOb+8t4tOvXxnqeXpFCByDgPnqKV5zPXS1XMF2+5qEAv7ZKrqK8BLAHbWsKHHOMt1hJ8K+EgYfRDKq72YvN01ST288ysUv8b5stRu8O5uC+KvZXtnlGrKc=",
)
class PrisonCrystal(gameProfile: GameProfile) : GameItemWithTexture(gameProfile)

/** Prison Pickaxes */

@GameItemInfo(
    id = "prison:pickaxe:normal",
    name = "Prison Normal Pickaxe",
    description = [
        "§7A normal pickaxe.",
        "§7Given by the §cPrison Wizard",
    ],
    rarity = GameItemRarity.UNCOMMON,
    material = Material.WOOD_PICKAXE,
    flags = [ ItemFlag.HIDE_ATTRIBUTES ],
    unbreakable = true
)
@GameItemPickaxe
class PrisonNormalPickaxe : ToolItem()

@GameItemInfo(
    id = "prison:pickaxe:cursed",
    name = "Prison Cursed Pickaxe",
    description = [
        "§7Crafted in the §c§l☠§c Humans Slaughter",
        "§7Cursed by §cPrison Witch",
        "§7",
        "§c§l☣§c Item Curse",
        "§8» §7Each time a block mined by this pickaxe",
        "§8» §7You get §cMiner Fatigue I§7 for §c5 seconds", // TODO Design a system for cursed items
    ],
    rarity = GameItemRarity.UNCOMMON,
    material = Material.WOOD_PICKAXE,
    flags = [ItemFlag.HIDE_ATTRIBUTES],
    unbreakable = true
)
@GameItemPickaxe
class PrisonCursedPickaxe : ToolItem(), GameItemOnBreak {
    override fun onBreak(player: Player) {
        player.run {
            if (hasPotionEffect(PotionEffectType.SLOW_DIGGING))
                return

            addPotionEffect(PotionEffect(PotionEffectType.SLOW_DIGGING, 20 * 5, 0), true)

            sendActionBar("§cMiner Fatigue§b I§7 -§c 5§e seconds")
        }
    }
}

@GameItemInfo(
    id = "prison:pickaxe:ultimate",
    name = "Prison Ultimate Pickaxe",
    description = [
        "§7Crafted in the §6§l☯§e Humans Agency",
        "§7Cursed by §3Agent H",
        "§9Break multiple blocks",
        "§7",
        "§c§l☣§c Item Curse",
        "§8» §7Each time a block mined by this pickaxe",
        "§8» §7You get §6Haste III§7 for §c5 seconds", // TODO Design a system for cursed items
    ],
    rarity = GameItemRarity.SPECIAL,
    material = Material.IRON_PICKAXE,
    flags = [ItemFlag.HIDE_ATTRIBUTES],
    unbreakable = true
)
@GameItemPickaxe
class PrisonUltimatePickaxe : ToolItem(), GameItemOnBreak {
    override fun onBreak(player: Player) {
        player.run {
            if (hasPotionEffect(PotionEffectType.FAST_DIGGING))
                return

            addPotionEffect(PotionEffect(PotionEffectType.FAST_DIGGING, 20 * 5, 2), true)

            sendActionBar("§6Haste§b III§7 -§c 5§e seconds")
        }
    }
}
