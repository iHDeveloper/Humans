package me.ihdeveloper.humans.core.entity

import me.ihdeveloper.humans.core.applyTexture
import me.ihdeveloper.humans.core.randomGameProfile
import org.bukkit.Location
import org.bukkit.entity.Player

class HubSelector(
    location: Location
) : CustomNPC(
    location,
    profile
) {
    companion object {
        private val profile = randomGameProfile().apply {
            applyTexture(
                texture = "eyJ0aW1lc3RhbXAiOjE1NTE2OTg0NDY4MjAsInByb2ZpbGVJZCI6IjNkNGEyODk5ZTU2MTRhMTVhZWZhZTI0MmJkZjM5YmIyIiwicHJvZmlsZU5hbWUiOiJpSERldmVsb3BlciIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzkwM2YyNzU3ZDQ4MjFiMDMwYmE0MTkxOWNjODJiNmE4NzhiM2FlODBmMjQ4YjI1YzY3MWZmNTg5NmVhZDZkMyJ9fX0=",
                signature = "sK5jDB2FFC0hTCLbJziRpC69t3psw7fMRwzi/9VrCEGyEWKqY9kXP3K8JablauQ1kcxVyZjTlPGWnd9aPAphDkcT6gnTBeIuawgdijzOFE4HUaZE14UFz4768cjAF83lF9DDViTsRH+32wZ4YYDgxXCI6aS4Hg3kGB7+Qz576NKNHjFsABk+u9RynLzbOfI1eZ/wQbA1LqWn8fhxXy1pmnHu3/LiApREvpHXHU6BVcqASVVCz/e+siBPXDBanwd/iy25HxHN+e/pmbnZUxIHpA0pKKJYHwZcRev2VxFm6EzmnQ6C3Z6KCfT1Y/7qSfZRxU5C7BK12d5TqmTouiTmc6BpjO9hdbpuHC4QJMyt6NFCiK3/Tzbvd3ZrYuxo4LOIrGA1V4b/EGnz6fr0cemnWkTAFX80RJe2a+Yrj5vwZJcU3k1bh0ODqdMQiLGSiDVLhJXh9Jp/sdJD5OHSJVGbHx6pHxs/TPlVBy4LIlydPlapR3zLiXC/eA5LR1MJxHHjtO2B3//C/+i3G7okjVWHt5/JVpHIJ1ILyF1b1h2hUSL0YOAwOQIgByd9dwcYSzIJiJYO3ZkcGvLP7dcLYQpNYALU/JGPj8HSYP1T2/H+7B5pJOIPebTi0U9GsEYGUPHBH/i5IFEjIz+Ss/VV8cXzGdcy+ipk7GxFsmxIMtM5LqM="
            )
        }
    }

    init {
        initNPC()
        initHologram("§bHub Selector", "§7Gate Keeper", "§e§lCOMING SOON")
    }

    override fun interact(player: Player) {
        player.apply {
            sendMessage("§7[NPC] §bHub Selector: §eThe prison popularity is too small for my services to be available.")
        }
    }
}
