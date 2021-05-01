package me.ihdeveloper.humans.core.system

import me.ihdeveloper.humans.core.Configuration
import me.ihdeveloper.humans.core.ConfigurationDeserialize
import me.ihdeveloper.humans.core.ConfigurationSerialize
import me.ihdeveloper.humans.core.System
import me.ihdeveloper.humans.core.entity.CustomNPC
import me.ihdeveloper.humans.core.entity.fromNPCType
import me.ihdeveloper.humans.core.util.toNMSPlayer
import me.ihdeveloper.spigot.devtools.api.DevTools
import net.minecraft.server.v1_8_R3.EntityPlayer
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo
import net.minecraft.server.v1_8_R3.PlayerConnection
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scoreboard.NameTagVisibility

const val TEAM_NPC = "@z_npc"

/**
 * Requires to be initialized after [ScoreboardSystem]
 */
class NPCSystem : System("Core/NPC"), Listener {
    data class NPCInfo(
        val type: String,
        val location: Location,
    ) : ConfigurationSerialize {
        companion object: ConfigurationDeserialize<NPCInfo> {
            override fun deserialize(data: Map<String, Any>) = NPCInfo(
                type = data["type"] as String,
                location = data["location"] as Location
            )
        }

        override fun serialize(): Map<String, Any> = mapOf(
            "type" to type,
            "location" to location
        )
    }

    companion object {
        private var plugin: JavaPlugin? = null
        private val config = Configuration("npcs")
        private val npcList = arrayListOf<CustomNPC>()
        private val npcInfoList = arrayListOf<NPCInfo>()

        fun scheduleRemovePacket(connection: PlayerConnection, npc: EntityPlayer) {
            Bukkit.getScheduler().runTaskLater(plugin!!, {
                connection.sendPacket(PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, npc))
            }, 20L)
        }

        fun add(info: NPCInfo) {
            info.apply {
                val npc = fromNPCType(type, location)

                if (npc === null)
                    return

                npcList.add(npc)
                npcInfoList.add(this)
            }

            Bukkit.getOnlinePlayers().forEach {
                for (npc in npcList) {
                    if (!npc.shouldTrack(it.location))
                        continue

                    npc.spawn(toNMSPlayer(it))
                }
            }
        }

        fun save() {
            val list = arrayListOf<Map<String, Any>>()
            npcInfoList.forEach { list.add(it.serialize()) }
            config.set("npcs", list)
            config.save()
        }
    }

    override fun init(plugin: JavaPlugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin)
        Companion.plugin = plugin
    }

    override fun lateInit(plugin: JavaPlugin) {
        config.load(logger)
        val rawNPCs = config.get<ArrayList<Map<String, Any>>>("npcs", arrayListOf())

        rawNPCs.forEach {
            add(NPCInfo.deserialize(it))
        }
    }

    override fun dispose() {
        npcList.forEach { it.die() }
        npcList.clear()
        npcInfoList.clear()
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    @Suppress("UNUSED")
    fun onJoin(event: PlayerJoinEvent) {
        event.player.run {
            scoreboard.getTeam(TEAM_NPC) ?: scoreboard.registerNewTeam(TEAM_NPC).apply {
                prefix = "§8[NPC] "
                nameTagVisibility = NameTagVisibility.HIDE_FOR_OTHER_TEAMS
            }

            for (npc in npcList) {
                npc.run {
                    if (npc.location.world.name == player.location.world.name && shouldTrack(player.location))
                        spawn(toNMSPlayer(player))
                }
            }
        }
    }

    @EventHandler
    @Suppress("UNUSED")
    fun onMove(event: PlayerMoveEvent) {
        DevTools.profileStart("NPCs")
        for (npc in npcList) {
            if (npc.location.world.name != event.to.world.name)
                continue

            if (npc.trackedPlayers.contains(event.player.entityId)) {
                if (!npc.shouldTrack(event.to)) {
                    npc.despawn(toNMSPlayer(event.player))
                    logger.debug("[NPC Tracker] Despawning ${event.player.name}...")
                }
                continue
            }

            npc.spawn(toNMSPlayer(event.player))
            logger.debug("[NPC Tracker] Spawning ${event.player.name}...")
        }
        DevTools.profileEnd("NPCs")
    }

    @EventHandler
    @Suppress("UNUSED")
    fun onTeleport(event: PlayerTeleportEvent) {
        Bukkit.getScheduler().runTaskLater(plugin, {
            for (npc in npcList) {
                if (npc.location.world.name != event.to.world.name) {
                    continue
                }

                if (npc.trackedPlayers.contains(event.player.entityId)) {
                    if (!npc.shouldTrack(event.to)) {
                        npc.despawn(toNMSPlayer(event.player))
                        logger.debug("[NPC Tracker] Despawning ${event.player.name} (teleportation)...")
                    }
                    continue
                }

                npc.spawn(toNMSPlayer(event.player))
                logger.debug("[NPC Tracker] Spawning ${event.player.name} (teleportation)...")
            }
        }, 2L)
    }

    @EventHandler
    @Suppress("UNUSED")
    fun onInteract(event: PlayerInteractAtEntityEvent) {
        event.apply {
            if (rightClicked.type !== EntityType.PLAYER) return
            val nmsPlayer = (rightClicked as CraftPlayer).handle
            if (nmsPlayer !is CustomNPC) return

            nmsPlayer.interact(player)
        }
    }

    @EventHandler
    @Suppress("UNUSED")
    fun onClick(event: EntityDamageByEntityEvent) {
        event.apply {
            if (entity.type !== EntityType.PLAYER || damager.type !== EntityType.PLAYER) return
            val nmsPlayer = (entity as CraftPlayer).handle
            if (nmsPlayer !is CustomNPC) return

            nmsPlayer.interact(damager as Player)
            event.isCancelled = true
        }
    }
}
