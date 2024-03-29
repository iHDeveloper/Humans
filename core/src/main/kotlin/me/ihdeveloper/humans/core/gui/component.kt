package me.ihdeveloper.humans.core.gui

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

/**
 * Represents a gui component in the inventory
 */
abstract class GUIComponent {

    /**
     * Returns an [ItemStack] to be rendered in the [GUIScreen]
     */
    abstract fun render(): ItemStack
}

/**
 * Represents a stateless image in the GUI
 */
class GUIImage(
    private val material: Material,
    private val amount: Int,
    private val data: Short,
    private val title: String,
    private val description: ArrayList<String> = arrayListOf(),
    private val flags: Array<ItemFlag>? = null
): GUIComponent() {

    override fun render(): ItemStack = ItemStack(material, amount, data).apply {
        itemMeta = itemMeta.apply {
            displayName = title
            lore = description

            flags?.forEach { addItemFlags(it) }
        }
    }
}

/**
 * Represents a component that requires player's object to render
 */
interface GUIRenderByPlayer {
    fun renderByPlayer(player: Player): ItemStack
}

/**
 * Represents a component that re-render when the player clicks on it
 */
interface GUIOnClick {

    /**
     * Returns true when the component needs to be re-rendered
     */
    fun onClick(player: Player): Boolean
}
