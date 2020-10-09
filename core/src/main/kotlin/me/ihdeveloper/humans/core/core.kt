package me.ihdeveloper.humans.core

import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.event.block.BlockBreakEvent
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
 * Handle the command for admin only
 */
abstract class AdminCommand(name: String): me.ihdeveloper.humans.core.Command(name) {

    override fun onCommand(p0: CommandSender?, p1: Command?, p2: String?, p3: Array<out String>?): Boolean {
        if (!p0!!.isOp) {
            p0.sendMessage("§cYou don't have permission to do this!")
            return true
        }

        return super.onCommand(p0, p1, p2, p3)
    }
}

/**
 * Reader/Writer for the game configurations
 */
class Configuration(val name: String) {
    private val file = File(dataFolder, "${name}.yml")
    private val config = YamlConfiguration()

    /**
     * Load the configuration from the file
     */
    fun load() = load(null)
    fun load(logger: GameLogger?) {
        logger?.info("Loading $name configuration in ${file.name}...")

        if (!file.isFile)
            file.createNewFile()

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

/**
 * Implementation for configuration serializable
 */
interface ConfigurationSerialize {
    fun serialize(): Map<String, Any>
}

/**
 * Implementation for configuration deserializable
 */
interface ConfigurationDeserialize<T> {
    fun deserialize(data: Map<String, Any>): T
}

/**
 * Represents the rarity of the game item
 */
enum class GameItemRarity(
    val color: ChatColor
) {
    COMMON(ChatColor.WHITE);
}

/**
 * Represents the render info and rules of the game item
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class GameItemInfo(
    val id: String,
    val name: String,
    val description: Array<String>,
    val rarity: GameItemRarity,

    val material: Material,
    val data: Short = 0,
)

/**
 * Represents an instance of an item in the game
 *
 * The instance performs stateless operations fired from events in the game
 */
open class GameItem {
    open val rarityPrefix: String? = null
}
