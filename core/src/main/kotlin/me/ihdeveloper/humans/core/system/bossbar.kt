package me.ihdeveloper.humans.core.system

import kotlin.random.Random
import me.ihdeveloper.humans.core.BossBar
import me.ihdeveloper.humans.core.System
import me.ihdeveloper.humans.core.entity.connection
import me.ihdeveloper.humans.core.setPrivateField
import me.ihdeveloper.humans.core.util.toNMS
import net.minecraft.server.v1_8_R3.DataWatcher
import net.minecraft.server.v1_8_R3.MathHelper
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityTeleport
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.plugin.java.JavaPlugin

private const val WITHER_TRACKER_DISTANCE = 48

private const val WITHER_ID = 64

private const val CUSTOM_NAME_KEY = 2
private const val CUSTOM_NAME_VISIBLE_KEY = 3
private const val HEALTH_KEY = 6

private const val WITHER_INVULNERABILITY_KEY = 20

/**
 * Represents data about the entity of the boss bar
 */
internal class BossBarMeta(
    internal val id: Int,
    internal var location: Location,
) {
    internal var dataWatcher = DataWatcher(null)
}

class BossBarSystem : System("Core/Boss-Bar"), Listener {
    companion object {
        private val players = mutableMapOf<String, BossBar>()

        private val random = Random(2030)
        private val metas = mutableMapOf<String, BossBarMeta>()

        fun spawn(bossBar: BossBar, player: Player) = spawn(bossBar, player, player.location)
        private fun spawn(bossBar: BossBar, player: Player, location: Location) {
            if (players.containsKey(player.name)) {
                destroy(player)
            }

            players[player.name] = bossBar
            bossBar.run {
                val meta = BossBarMeta(
                    id = random.nextInt(),
                    location = location.run {
                        direction.multiply(WITHER_TRACKER_DISTANCE).add(toVector()).toLocation(world)
                    }
                )

                metas[player.name] = meta

                player.toNMS().connection.run {
                    sendPacket(PacketPlayOutSpawnEntityLiving().apply {
                        setPrivateField(this, "a", meta.id)
                        setPrivateField(this, "b", WITHER_ID)

                        meta.location.also {
                            setPrivateField(this, "c", it.blockX)
                            setPrivateField(this, "d", MathHelper.floor(it.y * 32.0))
                            setPrivateField(this, "e", it.blockZ)

                            setPrivateField(this, "i", ((it.yaw * 256.0 / 360.0).toInt()).toByte())
                            setPrivateField(this, "j", ((it.pitch * 256.0 / 360.0).toInt()).toByte())
                            setPrivateField(this, "k", ((it.pitch * 256.0 / 360.0).toInt()).toByte())
                        }

                        setPrivateField(this, "f", 0)
                        setPrivateField(this, "g", 0)
                        setPrivateField(this, "h", 0)

                        setPrivateField(this, "l", meta.dataWatcher)
                    })

                    meta.dataWatcher.run {
                        /** Entity Flag */
                        a(0, (0 or 1 shl 5).toByte())

                        /** Entity living flags */
                        a(CUSTOM_NAME_VISIBLE_KEY, 1.toByte())
                        with (bossBar) { a(HEALTH_KEY, (current * 300F) / max) }
                        /** Maximum length in a name is 64 characters */
                        a(CUSTOM_NAME_KEY, bossBar.title)

                        /** Entity wither flags */
                        a(17, Integer(0))
                        a(18, Integer(0))
                        a(19, Integer(0))

                        a(WITHER_INVULNERABILITY_KEY, Integer(1000))
                    }
                }
            }
        }

        fun destroy(player: Player) {
            metas[player.name]?.let { meta ->
                players.remove(player.name)
                metas.remove(player.name)

                player.toNMS().connection.run {
                    sendPacket(PacketPlayOutEntityDestroy(meta.id))
                }
            }
        }

        private fun teleport(player: Player, to: Location) {
            metas[player.name]?.let { meta ->
                player.toNMS().connection.run {
                    meta.location = (to.direction.multiply(WITHER_TRACKER_DISTANCE).add(to.toVector()).toLocation(to.world))

                    sendPacket(PacketPlayOutEntityTeleport().apply {
                        setPrivateField(this, "a", meta.id)

                        meta.location.also {
                            setPrivateField(this, "b", MathHelper.floor(it.x * 32.0))
                            setPrivateField(this, "c", MathHelper.floor(it.y * 32.0))
                            setPrivateField(this, "d", MathHelper.floor(it.z * 32.0))

                            setPrivateField(this, "e", ((it.yaw * 256.0 / 360.0).toInt()).toByte())
                            setPrivateField(this, "f", ((it.pitch * 256.0 / 360.0).toInt()).toByte())
                        }

                        setPrivateField(this, "g", false)
                    })
                }
            }
        }
    }

    override fun init(plugin: JavaPlugin) {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    override fun dispose() {}

    @EventHandler(priority = EventPriority.LOWEST)
    @Suppress("UNUSED")
    fun onMove(event: PlayerMoveEvent) {
        event.run {
            teleport(player, event.to)
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    @Suppress("UNUSED")
    fun onTeleport(event: PlayerTeleportEvent) {
        event.run {
            players[player.name]?.run {
                destroy(player)

                spawn(this, player, to)
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    @Suppress("UNUSED")
    fun onQuit(event: PlayerQuitEvent) {
        event.run {
            destroy(event.player)
        }
    }
}
