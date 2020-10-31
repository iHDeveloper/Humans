package me.ihdeveloper.humans.core.util

import kotlin.math.abs
import kotlin.math.max
import me.ihdeveloper.humans.core.GameItemStack
import me.ihdeveloper.humans.core.registry.NullGameItem
import me.ihdeveloper.humans.core.registry.NullGameItemStack
import me.ihdeveloper.humans.core.registry.createItem
import me.ihdeveloper.humans.core.registry.getItemClass
import me.ihdeveloper.humans.core.registry.getItemInfo
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
fun Inventory.getNMSItem(index: Int): ItemStack? = (this as CraftInventory).inventory.getItem(index)

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

    if (nms === null)
        return null

    if (nms.tag === null)
        return NullGameItemStack(nms.save(NBTTagCompound()))

    nms.tag.run {
        if (!hasKey("ItemData"))
            return NullGameItemStack(nms.save(NBTTagCompound()))

        val data = getCompound("ItemData")!!

        data.run {
            val id = getString("id")

            val type = getItemClass(id)

            if (type === null)
                return NullGameItemStack(nms.save(NBTTagCompound()))

            if (type === NullGameItem::class)
                return NullGameItemStack(getCompound("Lost"))

            return GameItemStack(
                type = type,
                amount = nms.count
            )
        }
    }
}

/**
 * Adds a game item stack to the inventory
 *
 * The operation supports the amount to be 64 at most.
 */
fun Inventory.addGameItem(item: GameItemStack): Boolean {
    val itemInfo = getItemInfo(item.type) ?: return false
    var itemAmount = item.amount

    var firstEmptySlot = -1
    for (slot in 0 until size) {
        if (slot == 8)
            continue

        val current = getGameItem(slot)

        if (itemInfo.stackable) {
            if (current == null) {
                if (firstEmptySlot == -1) {
                    firstEmptySlot = slot
                }
                continue
            }

            if (item.type !== current.type)
                continue

            if (current.amount >= 64)
                continue

            val total = current.amount + itemAmount

            if (total > 64) {
                val diff = abs(64 - current.amount)

                /** Remove the taken amount to fill the stack */
                itemAmount -= diff
                item.amount -= diff

                /** Fill the stack and update it in the inventory */
                current.amount += diff
                setGameItem(slot, current)
            } else {
                /** Set the stack with total and update it in the inventory */
                current.amount = total
                setGameItem(slot, current)
                return true
            }
        } else {
            /** Finds the first empty slot and set the item in there since the item is unstackable */
            if (current == null) {
                setGameItem(slot, item)
                return true
            }
        }
    }

    /** If all the stacks are full. then add the item to the first empty slot */
    if (itemInfo.stackable && item.amount > 0 && firstEmptySlot != -1) {
        setGameItem(firstEmptySlot, item)
        return true
    }
    return false
}
