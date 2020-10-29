package me.ihdeveloper.humans.core.util

import me.ihdeveloper.humans.core.BossBar
import me.ihdeveloper.humans.core.GameRegion
import me.ihdeveloper.humans.core.gui.GUIScreen
import me.ihdeveloper.humans.core.system.BossBarSystem
import me.ihdeveloper.humans.core.system.FreezeSystem
import me.ihdeveloper.humans.core.system.GUISystem
import me.ihdeveloper.humans.core.system.ProfileSystem
import me.ihdeveloper.humans.core.system.RegionSystem
import me.ihdeveloper.humans.service.api.Profile
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.util.Vector

private val oldVelocity = mutableMapOf<String, Vector>()

/**
 * Returns the players' game profile
 */
val Player.profile: Profile?
    get() = ProfileSystem.profiles[name]

/**
 * Represents information about the region of the player's location
 */
var Player.region: GameRegion
    set(value) {
        RegionSystem.players[name] = value
    }
    get() = RegionSystem.players[name] ?: RegionSystem.unknown

/**
 * Freezes a player's position
 */
fun Player.freeze() {
    oldVelocity[name] = velocity
    velocity = velocity.zero()
    FreezeSystem.players.add(name)
}

/**
 * Unfreezes a player's position. If the player is frozen before.
 */
fun Player.unfreeze() {
    velocity = oldVelocity[name]
    oldVelocity.remove(name)
    FreezeSystem.players.remove(name)
}

/**
 * Opens the [GUIScreen] to the player
 */
fun Player.openScreen(screen: GUIScreen) {
    if (GUISystem.screens.containsKey(name)) {
        closeScreen()
    }

    GUISystem.screens[name] = screen
    openInventory(screen)
}

/**
 * Closes the [GUIScreen] from the player
 */
fun Player.closeScreen() = closeInventory()

fun Player.showBossBar(bossBar: BossBar) = BossBarSystem.spawn(bossBar, this)

fun Player.updateBossBar() = BossBarSystem.update(this)

fun Player.hideBossBar() = BossBarSystem.destroy(this)

/**
 * Sets a random game profile with texture data and signature. And, apply it to the skull item
 */
fun SkullMeta.setTexture(data: String, signature: String) {
    val gameProfile = randomGameProfile().apply {
        applyTexture(
            texture = data,
            signature = signature
        )
    }

    val field = javaClass.getDeclaredField("profile")
    field.isAccessible = true
    field.set(this, gameProfile)
}
