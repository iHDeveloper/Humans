package me.ihdeveloper.humans.mine.entity

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import me.ihdeveloper.humans.core.entity.CustomArmorStand
import me.ihdeveloper.humans.core.entity.event.EntityOnClick
import me.ihdeveloper.humans.core.entity.event.EntityOnInteract
import me.ihdeveloper.humans.core.entity.spawnNPCHologram
import me.ihdeveloper.humans.core.registry.spawnEntity
import me.ihdeveloper.humans.core.util.Conversation
import me.ihdeveloper.humans.core.util.setTexture
import org.bukkit.Color
import org.bukkit.Effect
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.SkullMeta

private const val CRYSTAL_YAW_SPEED = 3.5F
private const val WIZARD_TABLE_RADIUS = 1F

/**
 * A monster that manages the mine crystals
 */
class PrisonMineWizard(
    location: Location
) : CustomArmorStand(location), EntityOnClick, EntityOnInteract {
    companion object {
        private val messages = arrayOf(
            "§7[Wizard] §cOscar:§f My job is maintaining the mines in the prison",
            "§7[Wizard] §cOscar:§f And, controls the access to those mines",
            "§7[Wizard] §cOscar:§f You can get pass to other mines through me"
        )
    }

    private val holograms = spawnNPCHologram(location, "§cOscar", "§7Prison Mine Wizard", "§e§lCLICK")
    private val table = location.clone().subtract(2.0, 0.0, 0.0).block

    private val tableCrystalLocation = table.location.clone().add(0.5, 0.25, 0.5)
    private val tableParticleLocation = table.location.clone().add(0.5, 0.15, 0.5)

    private val crystals = mutableListOf(
        PrisonMineCrystal(tableCrystalLocation.clone()),
        PrisonMineCrystal(tableCrystalLocation.clone()),
        PrisonMineCrystal(tableCrystalLocation.clone()),
        PrisonMineCrystal(tableCrystalLocation.clone()),
    )

    /** Represents the current angle */
    private var angle = 0.0

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

        if (table.type != Material.ENDER_PORTAL_FRAME)
            table.type = Material.ENDER_PORTAL_FRAME

        updateCrystals()

        crystals.forEach { spawnEntity(it, false, null) }
    }

    override fun t_() {
        super.t_()

        world.world.spigot().playEffect(tableParticleLocation, Effect.WITCH_MAGIC)

        updateCrystals()

        angle += 3
        if (angle >= 360.0)
            angle = 0.0
    }

    override fun onClick(player: Player) {
        Conversation(player, messages).start()
    }

    override fun onInteract(player: Player) {
        player.sendMessage("§7[Wizard] §cOscar:§f Come back later to get pass to other mines!")
    }

    override fun die() {
        super.die()

        crystals.forEach { it.die() }
        holograms.forEach { it.die() }
    }

    private fun updateCrystals() {
        val anglePerCrystal = 360 / crystals.size

        var currentAngle = angle

        for (crystal in crystals) {
            val newLoc = crystal.crystalLocation.apply {
                val radian = (currentAngle * PI) / 180

                x = tableCrystalLocation.x + (WIZARD_TABLE_RADIUS * cos(radian))
                z = tableCrystalLocation.z + (WIZARD_TABLE_RADIUS * sin(radian))
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
