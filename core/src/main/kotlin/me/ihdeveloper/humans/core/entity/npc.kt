package me.ihdeveloper.humans.core.entity

import me.ihdeveloper.humans.core.corePlugin
import me.ihdeveloper.humans.core.util.Conversation
import me.ihdeveloper.humans.core.util.applyTexture
import me.ihdeveloper.humans.core.util.randomGameProfile
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

/**
 * Provides the ability to switch between hub servers
 */
class HubSelector(
    location: Location
) : CustomNPC(location, profile) {
    companion object {
        private val profile = randomGameProfile().apply {
            applyTexture(
                texture = "ewogICJ0aW1lc3RhbXAiIDogMTYwMTg4ODExODAzNSwKICAicHJvZmlsZUlkIiA6ICIzM2ViZDMyYmIzMzk0YWQ5YWM2NzBjOTZjNTQ5YmE3ZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJEYW5ub0JhbmFubm9YRCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9jZDYwMWIxY2E3ODVhMDE2MjA2ZDE3NTM5MTE4ODdlZTVkOGM0YTFmNmE3MGU5NDAyYTkxOTM3NDRlMjFiNTFlIgogICAgfQogIH0KfQ==",
                signature = "F4NZ33sUZtcs8hWhvy5OQYq8ptqWXlbpC+Stb2ZU/2lfHnUEDdNk4RwHbrLSOE03KFW1VTwqfnbuHWsFuqnlK8yv5CFOezN5JMvvAqvluo5aClmT8r19C+Ery2iD6y+o0NaBZpyYtyh4AuPuWmHz/RH6csnpxWb2Bny6GbXSGVe9jhHfZpbyGAVYorRYbmfsuQ0jNEziMwsAeArq7GhTt66EEeYXOFuyoFWjkTUCtPuTNLnXW9sw/YGoSi7bypUbRsV/Y95FvfcZS1K9cVXukpKIjQFXV3bW24T6RSolS9R57ILMMDmgRuh0Z++wnxwfXijoeR4ySFW9Na5sdMpFgpSGPE7il3K7SFroQyy+xrfzjVBVNLAea4JzOjBjIM5MP5Gk88ZkzJTxqz8QNqbjUiKWLtD7skbsyl1BgWxPzkrzAlvemaQ5v2BPNrJ/m3pYm/xmhAwnOGuJyozn5yxuGI4RHAK4T5qjKyXpiJPqKLZddiFGl/h98ZX3YWG56fY8u0cUnJqx1X6XKoIl6wyvZLb72xgjwvhLWjmaDpgHv5t0nI5+DiHrgDiD9Zj1SE9tdVqv9+vnCqxCrF39OiQHAJRtJrbzJtIysvVFmukghFzS0uUaTN0rplfUtlR1CdpVo/l10m4vilDHA8ew8MZJZXhw/Pq+qwy7AEGm+qvlMfk="
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

/**
 * An agent in the humans agency
 *
 * Represents a developer in the team
 */
class AgentDeveloper(
    location: Location
) : CustomNPC(location, profile) {
    companion object {
        private val profile = randomGameProfile().apply {
            applyTexture(
                texture = "ewogICJ0aW1lc3RhbXAiIDogMTYwMjQxMzAxMzExNywKICAicHJvZmlsZUlkIiA6ICJkODAwZDI4MDlmNTE0ZjkxODk4YTU4MWYzODE0Yzc5OSIsCiAgInByb2ZpbGVOYW1lIiA6ICJ0aGVCTFJ4eCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9mYmNiM2Q1NTY3NjM0OWM0ZmFkMDI1OTdmYzQ1ZDQ4MDQzY2M3NTQ1NGZhOGVmYTA2MDVjY2Q5NjBkZmM3MGFjIgogICAgfQogIH0KfQ==",
                signature = "ADXCLVbFPYeEqMNq+g6evhAhIQ3gXLixoH+FriqO2gp5tesz1lJZlCnyz+qDO4UZKOoW+slnA5P2ik7GWGi/7lZV4mx9hIbNfFbmjYUbjhVE0VmK6t74rfrW1SsUA+pI6MtcXjelrJ4W30bQlj+uq9lJkLuNEaDBz/WimnWZe5WQrMwcqEuNKdVTwtQCTj6LYDdhXVO8nwBiMQ1MRMtAPkKejrWdCv0Z68MCymuSV76ZzK9X9LbLrB3LzVeU8X4ln+In9aNJLYUCTude7w9XbsEf9hANXp0ZNusZ706keREMG/KwLuyrCn4tb2G+i52i1rL20rrMwiNIl62Xoug3Ra4jbq2CWKZ3Iqy8dhB6wjt0zDoT8oeyn4wOmcC08iI7CGLGCC0Vlq0yRYvDM5fw1IeFaQ6O3maxF7ZNMtmO5zv3Z8+8MOgk5a55rXivjqFIC8nn4jYWzYlK2PAPnAJTMXTOLN4uBtW1d5+o1i/Lkn7hf1xi0mKfbejjrV8FtgUOIHaIc9V4/3osxAefAdEkib15qJSR3Grnu5v3PWZwFSDqkEk1l3kwF7b7A1/e2O3dAcYDNsCHintCzr0VPIYdNp8WLRNsJxm6XhecInjB0CLqzf/qChOTwBVCCRgnfprERV8FGMHH8Uj66KaPFLyf4YGfQWbSNFNvtikIbK2bp0s="
            )
        }

        private val conversationMessages = arrayOf(
            "§7[NPC] §3iHDeveloper: §eHello! I am an agent in the HG aka Humans Agency.",
            "§7[NPC] §3iHDeveloper: §eIt's a secret agency for helping \"Humans\" stay alive in the prison",
            "§7[NPC] §3iHDeveloper: §eI will provide some of the agency services.",
            "§7[NPC] §3iHDeveloper: §eThese services will improves your life in the prison.",
            "§7[NPC] §3iHDeveloper: §eCome back later to use these services!"
        )
    }

    init {
        initNPC()
        initHologram("§3iHDeveloper", "§7Prison Agent", "§e§lCOMING SOON")

        val itemSkull = ItemStack(Material.SKULL_ITEM, 1, 3.toShort())
        itemSkull.apply {
            itemMeta = (itemMeta as SkullMeta).apply {
                owner = "iHDeveloper"
            }
        }

        (bukkitEntity as Player).inventory.helmet = itemSkull
    }

    override fun interact(player: Player) {
        Conversation(player, conversationMessages).start()
    }
}

