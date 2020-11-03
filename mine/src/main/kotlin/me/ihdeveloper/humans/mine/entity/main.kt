package me.ihdeveloper.humans.mine.entity

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import me.ihdeveloper.humans.core.GameItemStack
import me.ihdeveloper.humans.core.entity.CustomArmorStand
import me.ihdeveloper.humans.core.entity.event.EntityOnClick
import me.ihdeveloper.humans.core.entity.event.EntityOnInteract
import me.ihdeveloper.humans.core.entity.spawnNPCHologram
import me.ihdeveloper.humans.core.gui.GUIBlank
import me.ihdeveloper.humans.core.gui.GUIImage
import me.ihdeveloper.humans.core.gui.GUIShopSale
import me.ihdeveloper.humans.core.gui.screen
import me.ihdeveloper.humans.core.item.PrisonCoalPass
import me.ihdeveloper.humans.core.item.PrisonCrystal
import me.ihdeveloper.humans.core.item.PrisonNormalPickaxe
import me.ihdeveloper.humans.core.item.PrisonStone
import me.ihdeveloper.humans.core.registry.spawnEntity
import me.ihdeveloper.humans.core.util.Conversation
import me.ihdeveloper.humans.core.util.addGameItem
import me.ihdeveloper.humans.core.util.openScreen
import me.ihdeveloper.humans.core.util.setTexture
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Effect
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.SkullMeta

private const val CRYSTAL_YAW_SPEED = 3.5F
private const val WIZARD_TABLE_RADIUS = 1F
private const val WIZARD_TIMEOUT = 20L * 60L

/**
 * A monster that manages the mine crystals
 */
class PrisonMineWizard(
    location: Location,
    private val shopType: ShopType,
) : CustomArmorStand(location), EntityOnClick, EntityOnInteract {
    enum class ShopType {
        STONE,
        UNKNOWN;
    }

    companion object {
        private val messages = arrayOf(
            "§7[Wizard] §cOscar:§f My job is maintaining the mines in the prison",
            "§7[Wizard] §cOscar:§f And, controls the access to those mines",
            "§7[Wizard] §cOscar:§f You can get pass to other mines through me"
        )
    }

    private val holograms = spawnNPCHologram(location, "§cOscar", "§7Prison Mine Wizard", "§e§lCLICK")
    val table = PrisonMineWizardTable(location.clone().subtract(2.0, 0.0, 0.0).block)

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

    override fun onClick(player: Player) {
        Conversation(player, messages).start()
    }

    override fun onInteract(player: Player) {
        val screen = screen(3, "§8» Wizard Shop", player) {
            val blank = GUIBlank()

            for (y in 0..2)
                for (x in 0..8)
                    setItem(x, y, blank)

            when (shopType) {
                ShopType.STONE -> {
                    setItem(3, 1, GUIShopSale(
                        GameItemStack(PrisonCoalPass::class, 1),
                        GameItemStack(PrisonStone::class, 64),
                    ))

                    setItem(5, 1, GUIShopSale(
                        GameItemStack(PrisonNormalPickaxe::class, 1),
                        GameItemStack(PrisonStone::class, 32),
                    ))
                }
                ShopType.UNKNOWN -> {
                    setItem(4, 1, GUIImage(Material.BARRIER, 1, 0, "§8» §7Coming soon..."))
                }
            }
        }

        player.openScreen(screen)
    }

    override fun t_() {
        super.t_()

        table.onTick()
    }

    override fun die() {
        super.die()

        table.destroy()
        holograms.forEach { it.die() }
    }

}

/**
 * A table the handles the crystal entities
 */
class PrisonMineWizardTable(
    val block: Block
) {
    val size: Int
        get() = crystals.size

    private val crystalLocation = block.location.clone().add(.5, .25, .5)
    private val particleLocation = block.location.clone().add(.5, .15, .5)

    private val crystals = mutableListOf<PrisonMineCrystal>()
    private val players = mutableMapOf<String, Int>()

    private var angle = 0.0
    private var timeout = WIZARD_TIMEOUT

    init {
        if (block.type != Material.ENDER_PORTAL_FRAME)
            block.type = Material.ENDER_PORTAL_FRAME

        updateCrystals()

        crystals.forEach { spawnEntity(it, false, null) }
    }

    /** Adds crystal to the table */
    fun add(player: Player) {
        val crystal = PrisonMineCrystal(crystalLocation.clone())
        crystals.add(crystal)

        updateCrystals()
        players[player.name] = players.getOrDefault(player.name, 0) + 1

        spawnEntity(crystal, false, null)
    }

    /** Remove the crystals and give them to the player */
    fun remove(player: Player): Int {
        val count = players[player.name]
        if (count === null) {
            return -1
        }
        players.remove(player.name)

        val crystalItemStack = GameItemStack(PrisonCrystal::class)

        for (ignored in 1..count) {
            val crystal = crystals[size - 1]
            crystals.removeAt(size - 1)
            crystal.die()

            player.inventory.addGameItem(crystalItemStack)
        }

        player.sendMessage("§eYou got §7x$count $crystalItemStack§e from the wizard table")

        return count
    }

    /** De-spawns all crystals in the table */
    private fun reset() {
        players.keys.forEach {
            remove(Bukkit.getPlayerExact(it))
        }

        crystals.forEach { it.die() }
        crystals.clear()
    }

    internal fun onTick() {
        if (size <= 0) {
            timeout = WIZARD_TIMEOUT
        } else {
            particleLocation.world.spigot().playEffect(particleLocation, Effect.WITCH_MAGIC)
        }

        updateCrystals()

        timeout--
        if (timeout <= 0) {
            reset()
            angle = 0.0
            return
        }

        angle += 3
        if (angle >= 360.0)
            angle = 0.0
    }

    internal fun destroy() {
        crystals.forEach { it.die() }
    }

    private fun updateCrystals() {
        if (size <= 0)
            return

        val anglePerCrystal = 360 / crystals.size

        var currentAngle = angle

        for (crystal in crystals) {
            val newLoc = crystal.crystalLocation.apply {
                val radian = (currentAngle * PI) / 180

                x = crystalLocation.x + (WIZARD_TABLE_RADIUS * cos(radian))
                z = crystalLocation.z + (WIZARD_TABLE_RADIUS * sin(radian))
                yaw += CRYSTAL_YAW_SPEED
            }

            crystal.updateLocation(newLoc)
            currentAngle += anglePerCrystal
        }
    }
}

/**
 * Represents a crystal that rotates around the wizard's table
 */
class PrisonMineCrystal(
    location: Location
) : CustomArmorStand(location) {

    internal val crystalLocation: Location
        get() = super.location

    init {
        customName = "§ePrison Mine Crystal"
        customNameVisible = false
        isInvisible = true
        setGravity(false)
        setLocation()

        (getBukkitEntity() as ArmorStand).apply {
            helmet = ItemStack(Material.SKULL_ITEM, 1, 3.toShort()).apply {
                itemMeta = (itemMeta as SkullMeta).apply {
                    setTexture(
                        "ewogICJ0aW1lc3RhbXAiIDogMTYwMzYxMDQ0MzU4MywKICAicHJvZmlsZUlkIiA6ICIzM2ViZDMyYmIzMzk0YWQ5YWM2NzBjOTZjNTQ5YmE3ZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJEYW5ub0JhbmFubm9YRCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9lNDllYzdkODJiMTQxNWFjYWUyMDU5Zjc4Y2QxZDE3NTRiOWRlOWIxOGNhNTlmNjA5MDI0YzRhZjg0M2Q0ZDI0IgogICAgfQogIH0KfQ==",
                        "Mnf7PDLe+FPiO+wQ2St6XNRiiIXtZ3GuPTcLlM7pNQ6d6MXuzI7xXG24qaAMFuVwMB+F3dLYcaFlc+bWyi3Qm9msSq2mMUXdvzTamAslZHcdcTFNpppkYgdvkOhWK7W/amQyd2Q+pLDECe8Mg6gxBY17+xfaWlIynzEWEmHR+ye+hTC44kgiTZaYiRg7gpU002deY8WpX875cc5zJIroxVR52qHIV+suIMPwq47mpCp520J9R1HuYvvP/V3+PwL7skMlC1F/HHkG5A13fvSKMqq9XMsdqXR8qvWlcL5IQTS7ijtD9TZo8jcmhz/7HCXuJ912I1GqJp4hZ0Lqa0NB0TuI/giHr2i4yNzORe6oan47bpMXLoZWIrYZIOsF6wSObhwniF1jM/zUEkum9XswRImIvYYlmyLH+Kkh5uQJm244rOLPXmOZEid6PW5bhaSRpMOMpxboeOtjLbGC56Ev+DwoI37SrAYY6/LC7HwjVhvkcsLd/9BrF+Wl10bdLdsJEbd+TII59/45MM1x7+xgeAFU/ip0TjkMPfRLdNmfxOGssMFZOaM55iOb+8t4tOvXxnqeXpFCByDgPnqKV5zPXS1XMF2+5qEAv7ZKrqK8BLAHbWsKHHOMt1hJ8K+EgYfRDKq72YvN01ST288ysUv8b5stRu8O5uC+KvZXtnlGrKc="
                    )
                }
            }
        }
    }

    fun updateLocation(location: Location) {
        this.location.run {
            x = location.x
            y = location.y
            z = location.z
            yaw = location.yaw
            pitch = location.pitch
        }
        setLocation()
    }
}
