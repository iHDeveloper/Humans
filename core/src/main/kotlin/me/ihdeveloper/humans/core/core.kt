package me.ihdeveloper.humans.core

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin

/**
 * Represents a system in the game.
 *
 * It can be initialized and disposed. It contains a special logger.
 */
abstract class System(val name: String) {
    protected val logger = GameLogger(name)

    /**
     * Initialize the system
     */
    abstract fun init(plugin: JavaPlugin)

    /**
     * Dispose the system
     */
    abstract fun dispose()
}

/**
 * Handle the command
 */
abstract class Command(val name: String): CommandExecutor {

    override fun onCommand(p0: CommandSender?, p1: Command?, p2: String?, p3: Array<out String>?): Boolean = execute(p0, p1, p2, p3)

    abstract fun execute(sender: CommandSender?, cmd: Command?, label: String?, args: Array<out String>?): Boolean
}
