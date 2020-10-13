package me.ihdeveloper.humans.core.system

import me.ihdeveloper.humans.core.Configuration
import me.ihdeveloper.humans.core.ConfigurationDeserialize
import me.ihdeveloper.humans.core.ConfigurationSerialize
import me.ihdeveloper.humans.core.util.GameLogger
import me.ihdeveloper.humans.core.util.ITEMSTACK_AIR
import me.ihdeveloper.humans.core.System
import me.ihdeveloper.humans.core.command.CreateWarpCommand
import me.ihdeveloper.humans.core.command.GiveCommand
import me.ihdeveloper.humans.core.command.ItemInfoCommand
import me.ihdeveloper.humans.core.command.NPCSaveCommand
import me.ihdeveloper.humans.core.command.NPCSummonCommand
import me.ihdeveloper.humans.core.command.SetSpawnCommand
import me.ihdeveloper.humans.core.command.SetWarpDisplayNameCommand
import me.ihdeveloper.humans.core.command.SetWarpLocationCommand
import me.ihdeveloper.humans.core.command.SummonCommand
import me.ihdeveloper.humans.core.command.SummonSaveCommand
import me.ihdeveloper.humans.core.command.WarpSaveCommand
import me.ihdeveloper.humans.core.core
import me.ihdeveloper.humans.core.corePlugin
import me.ihdeveloper.humans.core.entity.CustomArmorStand
import me.ihdeveloper.humans.core.entity.CustomGiant
import me.ihdeveloper.humans.core.entity.CustomMineCart
import me.ihdeveloper.humans.core.entity.CustomNPC
import me.ihdeveloper.humans.core.entity.CustomSkeleton
import me.ihdeveloper.humans.core.entity.Hologram
import me.ihdeveloper.humans.core.entity.PrisonGuard
import me.ihdeveloper.humans.core.entity.WarpCart
import me.ihdeveloper.humans.core.entity.WitherSkull
import me.ihdeveloper.humans.core.entity.fromEntityType
import me.ihdeveloper.humans.core.registry.overrideEntity
import me.ihdeveloper.humans.core.registry.registerEntity
import me.ihdeveloper.humans.core.registry.spawnEntity
import me.ihdeveloper.humans.core.registry.summonedEntities
import me.ihdeveloper.humans.core.registry.summonedEntitiesInfo
import me.ihdeveloper.humans.core.util.Conversation
import me.ihdeveloper.humans.core.util.toNMSWorld
import net.minecraft.server.v1_8_R3.EntityArmorStand
import net.minecraft.server.v1_8_R3.EntityGiantZombie
import net.minecraft.server.v1_8_R3.EntityMinecartRideable
import net.minecraft.server.v1_8_R3.EntitySkeleton
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockBurnEvent
import org.bukkit.event.block.BlockDamageEvent
import org.bukkit.event.block.BlockDispenseEvent
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.block.BlockFadeEvent
import org.bukkit.event.block.BlockFormEvent
import org.bukkit.event.block.BlockFromToEvent
import org.bukkit.event.block.BlockGrowEvent
import org.bukkit.event.block.BlockIgniteEvent
import org.bukkit.event.block.BlockMultiPlaceEvent
import org.bukkit.event.block.BlockPhysicsEvent
import org.bukkit.event.block.BlockPistonExtendEvent
import org.bukkit.event.block.BlockPistonRetractEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryMoveItemEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerPickupItemEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.server.ServerListPingEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scoreboard.DisplaySlot

const val TEAM_DEV = "@1dev"
const val TEAM_BUILD = "@2build"
const val TEAM_MEMBER = "@9member"

/**
 * A system for registering the entities of the game core
 */
class CustomEntitySystem : System("Core/Custom-Entity"), Listener {
    data class EntityInfo(
        val type: String,
        val location: Location
    ) : ConfigurationSerialize {
        companion object: ConfigurationDeserialize<EntityInfo> {
            override fun deserialize(data: Map<String, Any>) = EntityInfo(
                data["type"] as String,
                data["location"] as Location
            )
        }

        override fun serialize(): Map<String, Any> = mapOf(
            "type" to type,
            "location" to location
        )
    }

    companion object {
        private var logger: GameLogger? = null
        private val config = Configuration("entities")

        fun save() {
            val entities = arrayListOf<Map<String, Any>>()
            for (info in summonedEntitiesInfo) {
                entities.add(info.serialize())
            }
            config.set("entities", entities)
            config.save(logger)
        }
    }

    override fun init(plugin: JavaPlugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin)
        Companion.logger = logger

         Bukkit.getWorlds().forEach { w ->
             w.entities.forEach {
                 it.remove()
             }
         }

        logger.info("Overriding entities...")

        /** Override base entities with custom ones */
        overrideEntity(EntityArmorStand::class, CustomArmorStand::class, logger)
        overrideEntity(EntitySkeleton::class, CustomSkeleton::class, logger)
        overrideEntity(EntityMinecartRideable::class, CustomMineCart::class, logger)
        overrideEntity(EntityGiantZombie::class, CustomGiant::class, logger)

        logger.info("Registering entities...")

        /** Register custom entities */
        registerEntity(Hologram::class, CustomArmorStand::class, logger)
        registerEntity(PrisonGuard::class, CustomSkeleton::class, logger)
        registerEntity(WarpCart::class, CustomMineCart::class, logger)
        registerEntity(WitherSkull::class, CustomGiant::class, logger)

        /** Loads the entities  */
        config.load(logger)
        val rawEntities = config.get<ArrayList<Map<String, Any>>>("entities", arrayListOf())

        for (rawEntity in rawEntities) {
            val info = EntityInfo.deserialize(rawEntity)
            val type = info.type
            val location = info.location
            logger.debug("Loading entity with info [type=$type, world=${location.world.name}, x=${location.x}, y=${location.y}, z=${location.z}]...")

            val entity = fromEntityType(type, location)
            if (entity == null) {
                logger.warn("Entity type not found: $type")
                continue
            }

            spawnEntity(entity, false, logger)
            summonedEntities.add(entity)
            summonedEntitiesInfo.add(info)
        }

    }

    override fun dispose() {
        for (summonedEntity in summonedEntities) {
            summonedEntity.bukkitEntity.remove()
        }

        summonedEntities.clear()
        summonedEntitiesInfo.clear()
    }

    /**
     * Prevent the game from spawning entities naturally
     */
    @EventHandler
    @Suppress("UNUSED")
    fun onSpawn(event: CreatureSpawnEvent) {
        if (event.spawnReason === CreatureSpawnEvent.SpawnReason.CUSTOM)
            return

        event.isCancelled = true
    }
}

/**
 * A system for registering commands
 */
class CommandSystem : System("Core/Command") {
    private val commands = arrayOf(
        /** Item Commands */
        GiveCommand(),
        ItemInfoCommand(),

        /** Summon Commands */
        SummonCommand(),
        SummonSaveCommand(),

        /** Warp Commands */
        CreateWarpCommand(),
        SetWarpDisplayNameCommand(),
        SetWarpLocationCommand(),
        WarpSaveCommand(),

        /** Spawn commands */
        SetSpawnCommand(),

        /** NPC commands */
        NPCSummonCommand(),
        NPCSaveCommand(),
    )

    override fun init(plugin: JavaPlugin) {
        logger.info("Registering command executors...")
        commands.forEach {
            logger.debug("Registering command executor for /${it.name}...")

            when {
                plugin.getCommand(it.name) != null -> plugin.getCommand(it.name).executor = it
                corePlugin!!.getCommand(it.name) != null -> corePlugin!!.getCommand(it.name).executor = it
                else -> logger.error("Failed to register command /${it.name}! It's not found in the plugin.yml!")
            }
        }
    }

    override fun dispose() {}
}

/**
 * A system for managing the block events in the game
 */
class BlockSystem : System("Core/Block"), Listener {

    override fun init(plugin: JavaPlugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin)
    }

    override fun dispose() {}

    /**
     * Cancel any block event.
     * The game doesn't want to be conflicted with vanilla block system
     */
    @EventHandler @Suppress("UNUSED") fun onEvent(event: BlockBreakEvent) = cancelEvent(event)
    @EventHandler @Suppress("UNUSED") fun onEvent(event: BlockPlaceEvent) = cancelEvent(event)
    @EventHandler @Suppress("UNUSED") fun onEvent(event: BlockPhysicsEvent) = cancelEvent(event)
    @EventHandler @Suppress("UNUSED") fun onEvent(event: BlockIgniteEvent) = cancelEvent(event)
    @EventHandler @Suppress("UNUSED") fun onEvent(event: BlockGrowEvent) = cancelEvent(event)
    @EventHandler @Suppress("UNUSED") fun onEvent(event: BlockFadeEvent) = cancelEvent(event)
    @EventHandler @Suppress("UNUSED") fun onEvent(event: BlockDispenseEvent) = cancelEvent(event)
    @EventHandler @Suppress("UNUSED") fun onEvent(event: BlockFormEvent) = cancelEvent(event)
    @EventHandler @Suppress("UNUSED") fun onEvent(event: BlockBurnEvent) = cancelEvent(event)
    @EventHandler @Suppress("UNUSED") fun onEvent(event: BlockExplodeEvent) = cancelEvent(event)
    @EventHandler @Suppress("UNUSED") fun onEvent(event: BlockDamageEvent) = cancelEvent(event)
    @EventHandler @Suppress("UNUSED") fun onEvent(event: BlockFromToEvent) = cancelEvent(event)
    @EventHandler @Suppress("UNUSED") fun onEvent(event: BlockMultiPlaceEvent) = cancelEvent(event)
    @EventHandler @Suppress("UNUSED") fun onEvent(event: BlockPistonExtendEvent) = cancelEvent(event)
    @EventHandler @Suppress("UNUSED") fun onEvent(event: BlockPistonRetractEvent) = cancelEvent(event)

    @Suppress("UNUSED")
    private fun cancelEvent(event: Cancellable) {
        event.isCancelled = true
    }
}

/**
 * An item for the game menu
 */
val GAME_MENU = ItemStack(Material.NETHER_STAR, 1).apply {
    itemMeta = itemMeta.apply {
        displayName = "§eGame Menu §7(Right click)"
        lore = arrayListOf(
            "§7View your profile in the game!",
            "§7Track your skills, collections, etc...",
            "",
            "§6Click to open!"
        )
    }
}

/**
 * A system for managing the game menu
 */
class MenuSystem : System("Core/Menu"), Listener {

    override fun init(plugin: JavaPlugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin)
    }

    override fun dispose() {}

    /**
     * Prevent the player from clicking on the menu
     */
    @EventHandler
    @Suppress("UNUSED")
    fun onClick(event: InventoryClickEvent) {
        if (event.clickedInventory !is PlayerInventory)
            return
        if (event.slot != 8)
            return

        event.isCancelled = true
        open(event.whoClicked as Player)
    }

    /**
     * Prevent the player from moving the item
     */
    @EventHandler
    @Suppress("UNUSED")
    fun onMoveItem(event: InventoryMoveItemEvent) {
        if (event.item.type !== Material.NETHER_STAR)
            return
        event.isCancelled = true
    }

    /**
     * Put the menu when the player joins the server
     */
    @EventHandler
    @Suppress("UNUSED")
    fun onJoin(event: PlayerJoinEvent) {
        event.player.inventory.setItem(8, GAME_MENU)
    }

    /**
     * Remove the menu when the player quits the server
     */
    @EventHandler
    @Suppress("UNUSED")
    fun onQuit(event: PlayerQuitEvent) {
        event.player.inventory.setItem(8, ITEMSTACK_AIR)
    }

    /**
     * Opens the menu when the player is right/left click the menu
     */
    @EventHandler
    @Suppress("UNUSED")
    fun onInteract(event: PlayerInteractEvent) {
        val player = event.player
        val action = event.action

        if (
            (action !== Action.RIGHT_CLICK_BLOCK && action !== Action.RIGHT_CLICK_AIR)
            && (action !== Action.LEFT_CLICK_BLOCK && action !== Action.LEFT_CLICK_AIR)
                )
            return

        if (event.item != GAME_MENU)
            return

        open(player)
    }

    /**
     * Opens the menu
     */
    private fun open(player: Player) {
        // TODO opens the menu to the player
        player.sendMessage("§cCurrently the game menu is disabled!")
        player.sendMessage("§cIt's under heavy development.")
    }
}

/**
 * A system for managing the player events
 */
class PlayerSystem : System("Core/Player"), Listener {
    companion object {
        var spawn: Location? = null

        private val messages = arrayOf(
            "§7You are in unknown location.",
            "§7You don't remember what happened to you.",
            "§7You seem to be in a disclosed place.",
            "§7There's a suspicious door. Try to open it!"
        )
    }
    private val config = Configuration("players")

    override fun init(plugin: JavaPlugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin)

        config.load(logger)
        spawn = config.get<Location?>("spawn", null)
    }

    override fun dispose() {
        spawn?.let { config.set("spawn", it) }
        config.save(logger)
    }

    /**
     * Sends a welcome message to the players.
     *
     * Prevent from the broadcasting the join message
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    @Suppress("UNUSED")
    fun onJoin(event: PlayerJoinEvent) {
        event.player.run {
            val profile = ProfileSystem.profiles[name]

            if (profile!!.new) {
                Conversation(player, messages).start()
                // TODO start a special scene for the new players!
            } else {
                sendMessage("§eWelcome back, §7Human§e!")
                sendMessage("")
                sendMessage("")
            }

            if (spawn != null) {
                (this as CraftPlayer).handle.spawnIn(toNMSWorld(spawn!!.world))
                teleport(spawn)
                compassTarget = spawn
            }
            else
                logger.warn("Spawn location is not set!")

            for (player in Bukkit.getOnlinePlayers()) {
                if (!player.isOp)
                    continue

                player.sendMessage("§8[§e@§8] §a+ §9${displayName}")
            }
        }

        event.joinMessage = null
    }

    /**
     * Prevent broadcasting the quit message
     */
    @EventHandler
    @Suppress("UNUSED")
    fun onQuit(event: PlayerQuitEvent) {
        for (player in Bukkit.getOnlinePlayers()) {
            if (!player.isOp)
                continue

            player.sendMessage("§8[§e@§8] §c- §9${player.displayName}")
        }

        event.player.inventory.clear()
        event.quitMessage = null
    }

    /**
     * Prevent PVP between players in the game
     */
    @EventHandler
    @Suppress("UNUSED")
    fun onPvP(event: EntityDamageByEntityEvent) {
        if (event.entityType !== EntityType.PLAYER)
            return
        if (event.damager.type !== EntityType.PLAYER)
            return
        event.isCancelled = true
    }

    /**
     * Prevent the player from picking up items
     */
    @EventHandler
    @Suppress("UNUSED")
    fun onPickup(event: PlayerPickupItemEvent) {
        event.isCancelled = true
    }

    /**
     * Prevent the player from dropping items
     */
    @EventHandler
    @Suppress("UNUSED")
    fun onDrop(event: PlayerDropItemEvent) {
        event.isCancelled = true
    }

    /**
     * Prevent the player from getting hunger
     */
    @EventHandler
    @Suppress("UNUSED")
    fun onFoodLevel(event: FoodLevelChangeEvent) {
        event.foodLevel = 20
    }

    /**
     * Prevent the player from dying
     */
    @EventHandler
    @Suppress("UNUSED")
    fun onDeath(event: PlayerDeathEvent) {
        event.apply {
            entity.health = 20.0
            if (spawn != null) entity.teleport(spawn)

            Bukkit.getOnlinePlayers().forEach {
                it.sendMessage("${it.displayName} §edied.")
            }
        }
    }

    /**
     * Prevent the player from getting any kind of damage
     */
    @EventHandler
    @Suppress("UNUSED")
    fun onDamage(event: EntityDamageEvent) {
        event.apply {
            if (entity.type !== EntityType.PLAYER) return
            if ((entity as CraftPlayer).handle is CustomNPC) return

            isCancelled = true
        }
    }

}

const val SERVER_MOTD =
"""§8» §e§lTHE HUMANS §8§l- §7§lv0.0-ALPHA
§8» §c§lGAME IS UNDER HEAVY DEVELOPMENT"""

/**
 * A system for handling login and ping events
 */
class LoginSystem : System("Core/Login"), Listener {

    override fun init(plugin: JavaPlugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin)
    }

    override fun dispose() {}

    // TODO Handle login event for loading profiles

    @EventHandler
    @Suppress("UNUSED")
    fun onPing(event: ServerListPingEvent) {
        event.motd = SERVER_MOTD
    }
}

/**
 * A system for handling player scoreboard
 */
class ScoreboardSystem : System("Core/Scoreboard"), Listener {
    override fun init(plugin: JavaPlugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin)
    }

    override fun dispose() {}

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        event.player.scoreboard = Bukkit.getScoreboardManager().newScoreboard.apply {
            val sidebar = registerNewObjective("sidebar", "dummy").apply {
                displayName = "§e§lTHE HUMANS"
                displaySlot = DisplaySlot.SIDEBAR
            }

            sidebar.getScore("§0").score = 1
            core.apply { if (serverName != null) sidebar.getScore("§9§7§oV0.0b - ${serverName!!.toUpperCase()}").score = 1 }

            val devTeam = registerNewTeam(TEAM_DEV).apply {
                prefix = "§7[DEV] §3"
            }

            val buildTeam = registerNewTeam(TEAM_BUILD).apply {
                prefix = "§d"
            }

            val memberTeam = registerNewTeam(TEAM_MEMBER).apply {
                prefix = "§9"
            }

            Bukkit.getOnlinePlayers().forEach {
                when (it.name) {
                    "iHDeveloper" -> devTeam
                    "iSDeveloper" -> buildTeam
                    else -> memberTeam
                }.addEntry(it.name)
            }
        }
    }
}
