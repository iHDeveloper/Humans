package me.ihdeveloper.humans.core.scene

import me.ihdeveloper.humans.core.Scene
import me.ihdeveloper.humans.core.util.GameLogger
import org.bukkit.entity.Player

/**
 * Represents a scene can be run individually meaning one player can see it.
 * While, others don't
 */
open class IndividualScene(
    val player: Player,
    name: String,
    logger: GameLogger
) : Scene(name, logger)
