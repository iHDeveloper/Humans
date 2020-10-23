package me.ihdeveloper.humans.core.system

import me.ihdeveloper.humans.core.System
import me.ihdeveloper.humans.core.gui.GUIImage
import me.ihdeveloper.humans.core.gui.GUIOverview
import me.ihdeveloper.humans.core.gui.GUIScreen
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryMoveItemEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.plugin.java.JavaPlugin

/**
 * A system for handling GUI infrastructure in the game
 */
class GUISystem : System("Core/GUI"), Listener {
    companion object {
        val screens = mutableMapOf<String, GUIScreen>()
    }

    override fun init(plugin: JavaPlugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin)
    }

    override fun dispose() {}

    @EventHandler(priority = EventPriority.HIGH)
    @Suppress("UNUSED")
    fun onComponentClick(event: InventoryClickEvent) {
        event.run {
            val screen = screens[whoClicked.name] ?: return

            val component = screen.getComponent(slot) ?: return

            if (component is GUIImage || component is GUIOverview) {
                isCancelled = true
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    @Suppress("UNUSED")
    fun onComponentMove(event: InventoryMoveItemEvent) {
        event.run {
            isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    @Suppress("UNUSED")
    fun onScreenClose(event: InventoryCloseEvent) {
        screens.remove(event.player.name)
    }
}
