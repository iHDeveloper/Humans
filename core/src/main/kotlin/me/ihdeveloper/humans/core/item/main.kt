package me.ihdeveloper.humans.core.item

import me.ihdeveloper.humans.core.GameItem

/**
 * Abstraction for natural items (e.g. Stone, Coal, Gold, etc...)
 */
abstract class NaturalItem : GameItem() {
    override val raritySuffix = "NATURAL"
}

/**
 * Abstraction for enchanted natural items (e.g. Enchanted Stone, Enchanted Coal, etc...)
 */
abstract class EnchantedNaturalItem : GameItem() {
    override val raritySuffix = "ENCHANTED NATURAL"
}

/**
 * Abstraction for tool items (e.g. pickaxe, axe, etc...)
 */
abstract class ToolItem : GameItem() {
    override val raritySuffix = "TOOL"
}

/**
 * Abstraction for mine pass items (e.g. coal pass, gold pass, etc...)
 */
abstract class MinePassItem : GameItem() {
    override val raritySuffix = "MINE PASS"
}
