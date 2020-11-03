package me.ihdeveloper.humans.core.gui

import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventoryCustom
import org.bukkit.entity.Player

/**
 * Represents a stateless inventory for GUI
 */
class GUIScreen(
    columns: Int,
    title: String,
    private val player: Player? = null
): CraftInventoryCustom(
    null,
    9 * columns,
    title
) {
    private val components = mutableMapOf<Int, GUIComponent>()

    fun setItem(x: Int, y: Int, component: GUIComponent) = setItem(index(x, y), component)
    fun setItem(index: Int, component: GUIComponent) {
        super.setItem(index, when (component) {
            is GUIRenderByPlayer -> component.renderByPlayer(player!!)
            else -> component.render()
        })
        components[index] = component
    }

    fun getComponent(x: Int, y: Int) = getComponent(index(x, y))
    fun getComponent(index: Int): GUIComponent? = components[index]

    private fun index(x: Int, y: Int): Int = (y * 9) + x
}
