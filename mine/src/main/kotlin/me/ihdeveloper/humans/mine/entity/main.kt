package me.ihdeveloper.humans.mine.entity

import me.ihdeveloper.humans.core.entity.CustomArmorStand
import me.ihdeveloper.humans.core.entity.spawnNPCHologram
import me.ihdeveloper.humans.core.util.setTexture
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.ArmorStand
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.SkullMeta

/**
 * A monster that manages the mine crystals
 */
class PrisonMineWizard(
    location: Location
) : CustomArmorStand(location) {

    private val holograms = spawnNPCHologram(location, "§cOscar", "§7Prison Mine Wizard", "§e§lCLICK")

    init {
        customName = "§cPrison Mine Wizard"
        customNameVisible = false
        isInvisible = true
        setGravity(false)
        setLocation()

        (getBukkitEntity() as ArmorStand).run {
            itemInHand = ItemStack(Material.BLAZE_ROD, 1)

            helmet = ItemStack(Material.SKULL_ITEM, 1, 3.toShort()).apply {
                itemMeta = (itemMeta as SkullMeta).apply {
                    setTexture(
                        "ewogICJ0aW1lc3RhbXAiIDogMTYwMzYwMjM2MjQ1MSwKICAicHJvZmlsZUlkIiA6ICI5MWZlMTk2ODdjOTA0NjU2YWExZmMwNTk4NmRkM2ZlNyIsCiAgInByb2ZpbGVOYW1lIiA6ICJoaGphYnJpcyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9jY2RhNDFiM2ZkOTI2NTMwMDcxMmMyNDE0MTE3YjRiZmM0Mzc0YWY3YzA3MzI3YTI5NzE2ODMzMzNlYjk1YjhhIgogICAgfQogIH0KfQ==",
                        "lHjodZMf82N84gbn9gETusDWjDpAp13pG6piTT5uGsvUabyPXgAWJmBwHg82zkTmp7AYkQRT8bIOKMLKuY7ZjSrLMJPlvIoQNTnKvRQRgY3snAV9KSMQCl4ojZ2iGeOAVHJ9+szTVtLv8TNqQJBMG2okGNhpVbB2IGSVb7QE2FgiNphc/5fNXkMxbIgJy6XfbEkUyu358wlUN37UKaG71LEEa9gMxXxGzgz6D0h/gM0TKmxMSR2wIddbROyR/9KAl5ku6IaJgirje7rfSPqLrkoMqRHxVMRPHKZnKLdBcK3L+WnpeDgX4cGkd52tdBBLAEuLlb/i6IIlBN1sK8BonpTaGOS4ytdccVJC6V83vDgMGpMu3RfZkCSgeYv2Z0wMBRSIQ8FzYF1fSwpmkr/8e10MqJYPaIEUdy03TR0qsQanU2NvCn2uia4qrMsSFqXddULRVa17e2ET4/w9LdxASPHNZrfpyuncT4pBKecQyKcTxy5E86Kls/4/03c+qwfgHrFJRlYVqm7QAwKB+KumVAC8fS0A9mYxlWtzAP3Za3WwQNBncVUgPy5J2fwNJQNdSElXdE2N65SWa7mg1awrEvgIO9o7tW3dkWBlX9Ym4NVOyYBMEibwJgSddLHI33+O6H4ra20kPUM1XVrf3UYgVCPL8J5h/kbWSx+6T+Zg/N8="
                    )
                }
            }

            chestplate = ItemStack(Material.LEATHER_CHESTPLATE, 1).apply {
                itemMeta = (itemMeta as LeatherArmorMeta).apply {
                    color = Color.BLACK
                }
            }
        }
    }

    override fun die() {
        super.die()

        holograms.forEach { it.die() }
    }

}
