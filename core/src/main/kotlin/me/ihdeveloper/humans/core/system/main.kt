package me.ihdeveloper.humans.core.system

import me.ihdeveloper.humans.core.Configuration
import me.ihdeveloper.humans.core.ConfigurationDeserialize
import me.ihdeveloper.humans.core.ConfigurationSerialize
import me.ihdeveloper.humans.core.GameRegion
import me.ihdeveloper.humans.core.System
import me.ihdeveloper.humans.core.command.CrashCommand
import me.ihdeveloper.humans.core.command.CreateWarpCommand
import me.ihdeveloper.humans.core.command.GiveCommand
import me.ihdeveloper.humans.core.command.ItemInfoCommand
import me.ihdeveloper.humans.core.command.NPCSaveCommand
import me.ihdeveloper.humans.core.command.NPCSummonCommand
import me.ihdeveloper.humans.core.command.PlaySceneCommand
import me.ihdeveloper.humans.core.command.RegionNewCommand
import me.ihdeveloper.humans.core.command.RegionSaveCommand
import me.ihdeveloper.humans.core.command.RegionSetCommand
import me.ihdeveloper.humans.core.command.SaveSceneCommand
import me.ihdeveloper.humans.core.command.SceneSetLocationCommand
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
import me.ihdeveloper.humans.core.entity.CustomPotion
import me.ihdeveloper.humans.core.entity.CustomSkeleton
import me.ihdeveloper.humans.core.entity.CustomWitch
import me.ihdeveloper.humans.core.entity.Hologram
import me.ihdeveloper.humans.core.entity.ItemHologram
import me.ihdeveloper.humans.core.entity.PrisonGuard
import me.ihdeveloper.humans.core.entity.PrisonWatcher
import me.ihdeveloper.humans.core.entity.PrisonWitch
import me.ihdeveloper.humans.core.entity.WarpCart
import me.ihdeveloper.humans.core.entity.event.EntityOnClick
import me.ihdeveloper.humans.core.entity.event.EntityOnInteract
import me.ihdeveloper.humans.core.entity.fromEntityType
import me.ihdeveloper.humans.core.gui.GUIImage
import me.ihdeveloper.humans.core.gui.GUIOverview
import me.ihdeveloper.humans.core.gui.GUIScreen
import me.ihdeveloper.humans.core.registry.overrideEntity
import me.ihdeveloper.humans.core.registry.registerEntity
import me.ihdeveloper.humans.core.registry.spawnEntity
import me.ihdeveloper.humans.core.registry.summonedEntities
import me.ihdeveloper.humans.core.registry.summonedEntitiesInfo
import me.ihdeveloper.humans.core.scene.IntroScene
import me.ihdeveloper.humans.core.util.GameLogger
import me.ihdeveloper.humans.core.util.ITEMSTACK_AIR
import me.ihdeveloper.humans.core.util.between
import me.ihdeveloper.humans.core.util.openScreen
import me.ihdeveloper.humans.core.util.profile
import me.ihdeveloper.humans.core.util.region
import net.minecraft.server.v1_8_R3.EntityArmorStand
import net.minecraft.server.v1_8_R3.EntityGiantZombie
import net.minecraft.server.v1_8_R3.EntityMinecartRideable
import net.minecraft.server.v1_8_R3.EntityPotion
import net.minecraft.server.v1_8_R3.EntitySkeleton
import net.minecraft.server.v1_8_R3.EntityWitch
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Entity
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
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerAchievementAwardedEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerPickupItemEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.server.ServerListPingEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scoreboard.DisplaySlot
import org.spigotmc.event.player.PlayerSpawnLocationEvent

const val TEAM_DEV = "@1dev"
const val TEAM_BUILD = "@2build"
const val TEAM_MEMBER = "@9member"

const val TEAM_REGION = "@region"

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
        Companion.logger = logger
        plugin.server.pluginManager.registerEvents(this, plugin)

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
        overrideEntity(EntityWitch::class, CustomWitch::class, logger)
        overrideEntity(EntityPotion::class, CustomPotion::class, logger)

        logger.info("Registering entities...")

        /** Register custom entities */
        registerEntity(Hologram::class, CustomArmorStand::class, logger)
        registerEntity(PrisonGuard::class, CustomSkeleton::class, logger)
        registerEntity(WarpCart::class, CustomMineCart::class, logger)
        registerEntity(ItemHologram::class, CustomGiant::class, logger)
        registerEntity(PrisonWatcher::class, CustomArmorStand::class, logger)
        registerEntity(PrisonWitch::class, CustomWitch::class, logger)
        registerEntity(PrisonWitch.Potion::class, CustomPotion::class, logger)
    }

    override fun lateInit(plugin: JavaPlugin) {
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
    @EventHandler(priority = EventPriority.HIGHEST)
    @Suppress("UNUSED")
    fun onSpawn(event: CreatureSpawnEvent) {
        event.run {
            if (spawnReason === CreatureSpawnEvent.SpawnReason.CUSTOM) {
                isCancelled = false
                return
            }

            isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    @Suppress("UNUSED")
    fun onClick(event: EntityDamageByEntityEvent) {
        val nmsEntity = event.entity.toNMS()
        if (nmsEntity is EntityOnClick && event.damager.type === EntityType.PLAYER) {
            nmsEntity.onClick(event.damager as Player)
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    @Suppress("UNUSED")
    fun onInteract(event: PlayerInteractAtEntityEvent) {
        val nmsEntity = event.rightClicked.toNMS()
        if (nmsEntity is EntityOnInteract) {
            nmsEntity.onInteract(event.player)
            event.isCancelled = true
        }
    }

    private fun Entity.toNMS() = (this as CraftEntity).handle!!
}

/**
 * A system for registering commands
 */
class CommandSystem : System("Core/Command") {
    private val commands = arrayOf(
        CrashCommand(),

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

        /** Scene commands */
        SceneSetLocationCommand(),
        PlaySceneCommand(),
        SaveSceneCommand(),

        /** Region commands */
        RegionNewCommand(),
        RegionSetCommand(),
        RegionSaveCommand(),
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

    override fun lateInit(plugin: JavaPlugin) {
        logger.info("Registering command executors (late-init)...")
        core.integratedPart?.commands?.forEach {
            logger.debug("Registering command executor for /${it.name} (late-init)...")

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
        event.run {
            if (clickedInventory !is PlayerInventory)
                return
            if (slot != 8)
                return

            isCancelled = true

            if(action === InventoryAction.PICKUP_ONE)
                open(whoClicked as Player)
        }
    }

    /**
     * Put the menu when the player joins the server
     */
    @EventHandler(priority = EventPriority.LOWEST)
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
        val screen = GUIScreen(4, "§8» Game Menu").apply {
            player.run {
                val prefix = scoreboard.getEntryTeam(name).prefix
                setItem(4, 1, GUIOverview(prefix, name, profile!!))
            }

            /** Skills button */
            setItem(3, 2, GUIImage(
                Material.STONE_PICKAXE,
                1,
                0,
                "§8» §eSkills",
                arrayListOf(
                    "§7Used to improve your abilities",
                    "§7such as mining luck chance...",
                    "§0",
                    "§cComing Soon"
                ),
                arrayOf(
                    ItemFlag.HIDE_ATTRIBUTES
                )
            ))

            /** Collections button */
            setItem(5, 2, GUIImage(
                Material.BOOKSHELF,
                1,
                0,
                "§8» §eCollections",
                arrayListOf(
                    "§7Check the available crafting recipes.",
                    "§7Each collection has its own!",
                    "§0",
                    "§cComing Soon"
                )
            ))

            setItem(8, 3, GUIImage(
                Material.SKULL_ITEM,
                1,
                1.toShort(),
                "§8» §ePrison Status",
                arrayListOf(
                    "§cThe game is under heavy development!",
                    "§0",
                    "§eVersion: §70.0B"
                )
            ))
        }

        player.openScreen(screen)
    }
}

/**
 * A system for managing the player events
 */
class PlayerSystem : System("Core/Player"), Listener {
    companion object {
        private val config = Configuration("players")

        var spawn: Location? = null

        fun save() {
            spawn?.run { config.set("spawn", this) }
            config.save()
        }
    }

    private val introScenes = mutableMapOf<String, IntroScene>()

    override fun init(plugin: JavaPlugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin)

        config.load(logger)
        spawn = config.get<Location?>("spawn", null)
    }

    override fun dispose() {}

    @EventHandler(priority = EventPriority.HIGHEST)
    @Suppress("UNUSED")
    fun onSpawnLocation(event: PlayerSpawnLocationEvent) {
        event.run {
            player.profile!!.run {
                if (core.serverName == "Hub" && new) {
                    val scene = IntroScene(player)

                    spawnLocation = scene.spawn
                    introScenes[player.name] = scene
                    return
                }
            }

            player.run {
                if (spawn != null) {
                    spawnLocation = spawn
                } else {
                    logger.warn("Spawn location is not set!")
                }
            }
        }
    }

    /**
     * Sends a welcome message to the players.
     *
     * Prevent from the broadcasting the join message
     */
    @EventHandler(priority = EventPriority.LOW)
    @Suppress("UNUSED")
    fun onJoin(event: PlayerJoinEvent) {
        event.player.run {
            val profile = player.profile!!

            if (profile.new) {
                /** Initialize the intro scene for the player and start it */
                if (core.serverName == "Hub") {
                    introScenes[name]!!.start()
                    introScenes.remove(name)
                }
            } else {
                if (core.serverName == "Hub")
                    sendMessage("§eWelcome back, §7\"Human\"§e!")
                sendMessage("")
                sendMessage("")

                foodLevel = 20
                health = 20.0
            }

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

            player.sendMessage("§8[§e@§8] §c- §9${event.player.displayName}")
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
    @EventHandler(priority = EventPriority.LOWEST)
    @Suppress("UNUSED")
    fun onFoodLevel(event: FoodLevelChangeEvent) {
        event.run {
            if (SceneSystem.individuals.contains(entity.name))
                return

            isCancelled = true
        }
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
                it.sendMessage("${entity.displayName} §died.")
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

    @EventHandler
    @Suppress("UNUSED")
    fun onAchievement(event: PlayerAchievementAwardedEvent) {
        event.isCancelled = true
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

    @EventHandler
    @Suppress("UNUSED")
    fun onPing(event: ServerListPingEvent) {
        event.motd = SERVER_MOTD
    }

    @EventHandler
    @Suppress("UNUSED")
    fun onLogin(event: PlayerLoginEvent) {
        core.integratedPart?.run {
            val profile = event.player.profile

            if (profile == null)  {
                event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, "§cFailed to connect to ${core.serverName}! §7(PROFILE_NOT_FOUND_2)")
                return
            }

            if (profile.new) {
                if (allowNewPlayers) {
                    event.allow()
                } else {
                    event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, "§cFailed to connect to ${core.serverName}! §7(NEW_PLAYER)")
                }
            }
        }
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

    @EventHandler(priority = EventPriority.LOW)
    @Suppress("UNUSED")
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

            val thisPlayerTeam = when (event.player.name) {
                "iHDeveloper" -> devTeam
                "iSDeveloper" -> buildTeam
                else -> memberTeam
            }

            Bukkit.getOnlinePlayers().forEach { player ->
                when (player.name) {
                    "iHDeveloper" -> devTeam
                    "iSDeveloper" -> buildTeam
                    else -> memberTeam
                }.also {
                    it.addEntry(player.name)

                    /** Adds our player to the other player's team */
                    val thisPlayerName = event.player.name
                    if (player.name != thisPlayerName) {
                        player.scoreboard.getTeam(thisPlayerTeam.name).addEntry(thisPlayerName)
                    }
                }
            }
        }
    }
}

/**
 * A system for handling chat messages
 */
class ChatSystem : System("Core/Chat"), Listener {

    override fun init(plugin: JavaPlugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin)
    }

    override fun dispose() {}

    @EventHandler(priority = EventPriority.LOWEST)
    @Suppress("UNUSED")
    fun onChat(event: AsyncPlayerChatEvent) {
        event.run {
            event.isCancelled = true

            if (SceneSystem.individuals.containsKey(player.name)) {
                player.sendMessage("§cYou can't send a message during a scene!")
                return
            }

            val prefix = player.run { scoreboard.getEntryTeam(name)?.prefix ?: "§9" }
            val message = "$prefix${player.name} » §r${message}"

            /** Optimize the broadcasting without formatting */
            for (player in Bukkit.getOnlinePlayers()) {
                if (SceneSystem.individuals.containsKey(player.name))
                    continue

                player.sendMessage(message)
            }
        }
    }
}

/**
 * A system for handling regions
 */
class RegionSystem : System("Core/Region"), Listener {
    companion object {
        private val config = Configuration("regions")
        private val emptyLocation = Location(null, 0.0, 0.0, 0.0)

        val unknown = GameRegion(
            "unknown",
            "§7Unknown",
            emptyLocation,
            emptyLocation,
        )

        val players = mutableMapOf<String, GameRegion>()
        val regions = mutableListOf<GameRegion>()

        fun save() {
            config.run {
                set("regions", arrayListOf<Map<String, Any>>().apply {
                    for (region in regions)
                        add(region.serialize())
                })
                save()
            }
        }
    }

    override fun init(plugin: JavaPlugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin)

        config.load(logger)
        for (data in config.get("regions", arrayListOf<Map<String, Any>>())) {
            regions.add(GameRegion.deserialize(data))
        }
    }

    override fun dispose() {}

    @EventHandler
    @Suppress("UNUSED")
    fun onJoin(event: PlayerJoinEvent) {
        event.player.scoreboard.run {
            event.player.run {
                for (gameRegion in regions) {
                    if (!location.between(gameRegion.from, gameRegion.to))
                        continue

                    if (gameRegion !== region) {
                        region = gameRegion
                    }
                }

                if (region != unknown) {
                    region = unknown
                }
            }

            val sidebar = getObjective(DisplaySlot.SIDEBAR)!!
            registerNewTeam(TEAM_REGION).apply {
                prefix = "§8➤ "
                suffix = event.player.region.displayName

                "§r".also {
                    addEntry(it)
                    sidebar.getScore(it).score = 7
                }
            }
        }
    }

    @EventHandler
    @Suppress("UNUSED")
    fun onMove(event: PlayerMoveEvent) {
        event.player.run {
            regions.forEach {
                if (location.between(it.from, it.to)) {
                    if (it !== region) {
                        region = it
                        scoreboard.getTeam(TEAM_REGION).suffix = it.displayName
                    }
                    return
                }
            }

            if (region != unknown) {
                scoreboard.getTeam(TEAM_REGION).suffix = "§7Unknown"
                region = unknown
            }
            return
        }
    }
}
