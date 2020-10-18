package me.ihdeveloper.humans.core.util

import me.ihdeveloper.humans.core.system.FreezeSystem
import org.bukkit.entity.Player

/**
 * Freezes a player's position
 */
fun Player.freeze() = FreezeSystem.players.add(name)

/**
 * Unfreezes a player's position. If the player is frozen before.
 */
fun Player.unfreeze() = FreezeSystem.players.remove(name)
