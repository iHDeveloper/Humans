package me.ihdeveloper.humans.core.system

import me.ihdeveloper.humans.core.System
import me.ihdeveloper.humans.core.gui.GUIImage
import me.ihdeveloper.humans.core.gui.GUIOnClick
import me.ihdeveloper.humans.core.gui.GUIOverview
import me.ihdeveloper.humans.core.gui.GUIScreen
import me.ihdeveloper.humans.core.gui.GUIShopSale
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
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

            if (action !== InventoryAction.PICKUP_ONE
                && action !== InventoryAction.PICKUP_SOME
                && action !== InventoryAction.PICKUP_HALF
                && action !== InventoryAction.PICKUP_ALL) {
                isCancelled = true
                return
            }

            if (component !is GUIOnClick) {
                isCancelled = true
                return
            }

            if (component.onClick(whoClicked as Player)) {

                /** Re-renders the component on the screen */
                screen.setItem(slot, component)
                isCancelled = true
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    @Suppress("UNUSED")
    fun onScreenClose(event: InventoryCloseEvent) {
        screens.remove(event.player.name)
    }
}
