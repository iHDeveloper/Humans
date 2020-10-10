package me.ihdeveloper.humans.core.item

import me.ihdeveloper.humans.core.GameItem

/**
 * Abstraction for natural items (e.g. Stone, Coal, Gold, etc...)
 */
abstract class NaturalItem : GameItem() {
    override val raritySuffix = "NATURAL"
}

/**
 * Abstraction for tool items (e.g. pickaxe, axe, etc...)
 */
abstract class ToolItem : GameItem() {
    override val raritySuffix = "TOOL"
}
