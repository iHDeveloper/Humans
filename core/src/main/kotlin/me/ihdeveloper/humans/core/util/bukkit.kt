package me.ihdeveloper.humans.core.util

import me.ihdeveloper.humans.core.system.FreezeSystem
import me.ihdeveloper.humans.core.system.ProfileSystem
import me.ihdeveloper.humans.service.api.Profile
import org.bukkit.entity.Player
import org.bukkit.util.Vector

private val oldVelocity = mutableMapOf<String, Vector>()

/**
 * Returns the players' game profile
 */
val Player.profile: Profile?
    get() = ProfileSystem.profiles[name]

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
