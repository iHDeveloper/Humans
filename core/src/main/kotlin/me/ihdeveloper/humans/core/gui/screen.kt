package me.ihdeveloper.humans.core.gui

import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventoryCustom

/**
 * Represents a stateless inventory for GUI
 */
class GUIScreen(
    columns: Int,
    title: String
): CraftInventoryCustom(
    null,
    9 * columns,
    title
) {
    private val components = mutableMapOf<Int, GUIComponent>()

    fun setItem(x: Int, y: Int, component: GUIComponent) {
        val index = index(x, y)
        super.setItem(index, component.render())
        components[index] = component
    }

    fun getComponent(x: Int, y: Int) = getComponent(index(x, y))
    fun getComponent(index: Int): GUIComponent? = components[index]

    private fun index(x: Int, y: Int): Int = (y * 9) + x
}
