package me.ihdeveloper.humans.core.gui

import org.bukkit.entity.Player

inline fun screen(columns: Int, title: String, block: GUIScreen.() -> Unit): GUIScreen = screen(columns, title, null, block)
inline fun screen(columns: Int, title: String, player: Player?, block: GUIScreen.() -> Unit): GUIScreen {
    return GUIScreen(columns, title, player).apply { block(this) }
}

