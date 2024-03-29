package me.ihdeveloper.humans.core

import com.mojang.authlib.GameProfile
import java.io.File
import kotlin.reflect.KClass
import me.ihdeveloper.humans.core.registry.getItemInfo
import me.ihdeveloper.humans.core.registry.getItemInstance
import me.ihdeveloper.humans.core.system.SceneSystem
import me.ihdeveloper.humans.core.util.GameLogger
import me.ihdeveloper.spigot.devtools.api.DevTools
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
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
     * Initialized after the integrated part systems has been initialized
     */
    open fun lateInit(plugin: JavaPlugin) {}

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
    var isLoaded = false

    /**
     * Load the configuration from the file
     */
    fun load() = load(null)
    fun load(logger: GameLogger?) {
        logger?.info("Loading $name configuration in ${file.name}...")

        if (!file.isFile)
            file.createNewFile()

        config.load(file)
        isLoaded = true
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
    COMMON(ChatColor.WHITE),
    UNCOMMON(ChatColor.GREEN),
    RARE(ChatColor.BLUE),
    EPIC(ChatColor.DARK_PURPLE),
    SPECIAL(ChatColor.RED);
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
    val flags: Array<ItemFlag> = [],
    val unbreakable: Boolean = false,
    val stackable: Boolean = false,
)

/**
 * Represents information about the skull texture.
 *
 * The item material should be [Material.SKULL_ITEM] with the data value of 3
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class GameItemTexture(
    val texture: String,
    val signature: String,
)

/**
 * Represents that the item is a pickaxe
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class GameItemPickaxe

/**
 * Represents an instance of an item in the game
 *
 * The instance performs stateless operations fired from events in the game
 */
open class GameItem {
    open val raritySuffix: String? = null

    override fun toString(): String {
        getItemInfo(this::class)?.let {
            return "${it.rarity.color}${it.name}"
        } ?: return "§7Unknown Item"
    }
}

/**
 * Represents an instance of an item with texture in the game
 */
open class GameItemWithTexture(
    val gameProfile: GameProfile
) : GameItem()

/**
 * A stateful instance of the game item to avoid using [ItemStack]
 *
 */
open class GameItemStack(
    val type: KClass<out GameItem>,
    var amount: Int = 1,
) {
    val isPickaxe: Boolean
        get() {
            for (annotation in type.annotations) {
                if (annotation.annotationClass === GameItemPickaxe::class)
                    return true
            }
            return false
        }

    override fun toString(): String {
       getItemInstance(this.type)?.let {
           return it.toString()
       } ?: return "§7Unknown Item"
    }
}

/**
 * Handles the item on break event on the [GameItem]
 */
interface GameItemOnBreak {
    fun onBreak(player: Player)
}

/**
 * Represents the state of the scene
 */
enum class SceneState {
    NOT_STARTED,
    RUNNING,
    PAUSED,
    STOPPED;
}

/**
 * Contains actions that represents a scene in the game
 */
open class Scene(
    private val name: String,
    protected val logger: GameLogger
) : Runnable {

    /** State of the scene */
    var state = SceneState.NOT_STARTED

    /** A frame is something that happens at a certain time since the scene started */
    private val frames = mutableMapOf<Long, () -> Unit>()

    private var currentFrame: Long = 0
    private var cancelThisTick = false

    open fun start() {
        logger.debug("Starting $name...")
        state = SceneState.RUNNING

        frames[0L]?.invoke()

        currentFrame = 1
        schedule()
    }

    fun pause() {
        logger.debug("Pausing $name...")
        state = SceneState.PAUSED
    }

    fun resume() {
        logger.debug("Resuming $name...")

        state = SceneState.RUNNING

        frames[currentFrame]?.invoke()
        currentFrame++
    }

    open fun stop() {
        logger.debug("Stopping $name...")
        state = SceneState.STOPPED

        frames[-1L]?.invoke()

        frames.clear()
    }

    override fun run() {
        DevTools.profileStart("Scene/$name")
        if (state === SceneState.PAUSED) {
            frames[-2L]?.invoke()
            schedule()
            DevTools.profileEnd("Scene/$name")
            return
        } else if (state !== SceneState.RUNNING) {
            DevTools.profileEnd("Scene/$name")
            return
        }

        if (cancelThisTick) {
            cancelThisTick = false
            schedule()
            DevTools.profileEnd("Scene/$name")
            return
        }

        frames[-2L]?.invoke()
        frames[currentFrame]?.invoke()
        currentFrame++

        schedule()
        DevTools.profileEnd("Scene/$name")
    }

    /**
     * Implements a frame with its action in the scene's memory
     *
     * It's possible for a frame to start, pause and stop the scene
     */
    protected fun frame(ticks: Long, block: () -> Unit) = frames.set(ticks, block)

    /**
     * Represents the first frame of the scene to initialize some stuff for the scene to work
     */
    protected fun initFrame(block: () -> Unit) = frame(0L, block)

    /**
     * Represents the frame for disposing the references to the objects of the scene
     */
    protected fun disposeFrame(block: () -> Unit) = frame(-1L, block)

    /**
     * Called before the frame's being executed
     */
    protected fun everyFrame(block: () -> Unit) = frame(-2L, block)

    private fun schedule() = Bukkit.getScheduler().runTaskLater(SceneSystem.plugin, this, 1L)
}

/**
 * Represents the metadata of scene companion class
 */
interface SceneMeta {
    val config: Configuration

    fun save()
}

/**
 * Represents a region in the game
 */
data class GameRegion(
    val name: String,
    val displayName: String,
    val from: Location,
    val to: Location,
) : ConfigurationSerialize {
    companion object : ConfigurationDeserialize<GameRegion> {
        override fun deserialize(data: Map<String, Any>) = GameRegion(
            name = data["name"] as String,
            displayName = data["displayName"] as String,
            from = data["from"] as Location,
            to = data["to"] as Location
        )
    }

    override fun serialize(): Map<String, Any> = mapOf(
        "name" to name,
        "displayName" to displayName,
        "from" to from,
        "to" to to
    )
}

/**
 * Represents the boss bar on top of the player's screen
 */
data class BossBar(
    var title: String,
    var current: Int = 0,
    var max: Int = 100,
)

/**
 * Represents an entity with different states aka stateful entity
 */
interface CustomStatefulEntity {
    fun load(state: Map<String, Any>)
    fun store(state: MutableMap<String, Any>)
}
