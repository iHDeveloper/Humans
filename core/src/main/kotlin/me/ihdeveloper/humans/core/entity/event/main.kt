package me.ihdeveloper.humans.core.entity.event

import org.bukkit.entity.Player

/**
 * Triggers when the entity has been clicked by the player (left click)
 */
interface EntityOnClick {
    fun onClick(player: Player)
}

/**
 * Triggers when the entity has been interacted by the player (right click)
 */
interface EntityOnInteract {
    fun onInteract(player: Player)
}
