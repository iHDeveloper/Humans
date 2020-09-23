package me.ihdeveloper.humans.core

import org.bukkit.Bukkit
import org.bukkit.ChatColor

/** Minecraft Colors */
val COLOR_AQUA = ChatColor.AQUA
val COLOR_CYAN = ChatColor.DARK_AQUA
val COLOR_RED = ChatColor.RED
val COLOR_GOLD = ChatColor.GOLD
val COLOR_GRAY = ChatColor.GRAY
val COLOR_RESET = ChatColor.RESET
val COLOR_YELLOW = ChatColor.YELLOW
val COLOR_WHITE = ChatColor.WHITE

/**
 * An utility to help in logging the game actions
 */
class GameLogger(private val name: String) {

    /**
     * Prints an info message to the logger
     */
    fun info(message: String) = print("${COLOR_AQUA}INFO", message)

    /**
     * Prints a warning message to the logger
     */
    fun warn(message: String) = print("${COLOR_YELLOW}WARN", message)

    /**
     * Prints an error message to the logger
     */
    fun error(message: String) = print("${COLOR_RED}ERR", message)

    /**
     * Prints a debug message to the logger
     */
    fun debug(message: String) = print("${COLOR_GOLD}DEBUG", message)

    private fun print(prefix: String, message: String) = Bukkit.getConsoleSender().sendMessage(
        "${COLOR_GRAY}[" +
        "${COLOR_GOLD}Humans" +
        "${COLOR_GRAY}:" +
        "$COLOR_WHITE$name" +
        "${COLOR_GRAY}:" +
        "$COLOR_RESET$prefix" +
        "${COLOR_GRAY}] " +
        "$COLOR_RESET$message"
    )
}
