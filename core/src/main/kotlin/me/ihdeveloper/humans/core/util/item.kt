package me.ihdeveloper.humans.core.util

import me.ihdeveloper.humans.core.GameItemStack
import me.ihdeveloper.humans.core.registry.NullGameItem
import me.ihdeveloper.humans.core.registry.NullGameItemStack
import me.ihdeveloper.humans.core.registry.createItem
import me.ihdeveloper.humans.core.registry.getItemClass
import net.minecraft.server.v1_8_R3.ItemStack
import net.minecraft.server.v1_8_R3.NBTTagCompound
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventory
import org.bukkit.inventory.Inventory

typealias NMSItemStack = ItemStack

/**
 * Sets a NMS item directly from inventory
 */
fun Inventory.setNMSItem(index: Int, nmsItem: NMSItemStack) = (this as CraftInventory).inventory.setItem(index, nmsItem)

/**
 * Gets a NMS item directly from inventory
 */
fun Inventory.getNMSItem(index: Int): ItemStack = (this as CraftInventory).inventory.getItem(index)

/**
 * Sets a game item in the inventory
 */
fun Inventory.setGameItem(index: Int, item: GameItemStack) {
    val nmsItem = createItem(item.type, item.amount)

    /** Include any state of the item in NBT */
    nmsItem.tag.run {
        val data = getCompound("ItemData")!!

        if (item is NullGameItemStack) data.set("Lost", item.nbt)
    }

    setNMSItem(index, nmsItem)
}

/**
 * Gets a game item directly from inventory
 *
 * Returns [NullGameItem] when the game item is unknown
 */
fun Inventory.getGameItem(index: Int): GameItemStack? {
    val nms = getNMSItem(index)

    nms.tag.run {
        val data = getCompound("ItemData")

        if (data === null) {
            return NullGameItemStack(nms.save(NBTTagCompound()))
        }

        data.run {
            val id = getString("id")

            val type = getItemClass(id)

            if (type === null)
                return NullGameItemStack(nms.save(NBTTagCompound()))

            if (type === NullGameItem::class) {
                return NullGameItemStack(getCompound("Lost"))
            }

            return GameItemStack(
                type = type,
                amount = nms.count
            )
        }
    }
}
