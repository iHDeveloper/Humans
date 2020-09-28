package me.ihdeveloper.humans.core

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

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

/**
 * Reader/Writer for the game configurations
 */
class Configuration(val name: String) {
    private val file = File(core.dataFolder, "${name}.yml")
    private val config = YamlConfiguration()

    /**
     * Load the configuration from the file
     */
    fun load() = load(null)
    fun load(logger: GameLogger?) {
        logger?.info("Loading $name configuration in ${file.name}...")
        config.load(file)
    }

    /**
     * Gets a value from the configuration
     */
    fun <T> get(name: String): T {
        @Suppress("UNCHECKED_CAST")
        return config.get(name) as T
    }

    /**
     * Gets a value from the configuration
     */
    fun <T> get(name: String, default: T): T {
        @Suppress("UNCHECKED_CAST")
        return config.get(name, default) as T
    }

    /**
     * Sets a value in the configuration
     */
    fun set(name: String, value: Any) {
        config.set(name, value)
    }

    /**
     * Saves the configuration to the file
     */
    fun save() = save(null)
    fun save(logger: GameLogger?) {
        logger?.info("Saving $name configuration in ${file.name}...")
        config.save(file)
    }
}
