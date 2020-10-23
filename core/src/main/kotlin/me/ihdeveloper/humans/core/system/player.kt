package me.ihdeveloper.humans.core.system

import me.ihdeveloper.humans.core.System
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin

/**
 * A system for handling frozen players
 */
class FreezeSystem : System("Core/Freeze"), Listener {
    companion object {
        val players = mutableSetOf<String>()
    }

    override fun init(plugin: JavaPlugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin)
    }

    override fun dispose() {}

    @EventHandler(priority = EventPriority.HIGHEST)
    @Suppress("UNUSED")
    fun onMove(event: PlayerMoveEvent) {
        event.run {
            if (!players.contains(player.name))
                return

            isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    @Suppress("UNUSED")
    fun onQuit(event: PlayerQuitEvent) {
        players.remove(event.player.name)
    }
}
