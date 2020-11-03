package me.ihdeveloper.humans.core.util

import kotlin.math.abs
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
fun Inventory.setNMSItem(index: Int, nmsItem: NMSItemStack?) = (this as CraftInventory).inventory.setItem(index, nmsItem)

/**
 * Gets a NMS item directly from inventory
 */
fun Inventory.getNMSItem(index: Int): ItemStack? = (this as CraftInventory).inventory.getItem(index)

/**
 * Sets a game item in the inventory
 */
fun Inventory.setGameItem(index: Int, item: GameItemStack?) {
    if (item === null) {
        setNMSItem(index, null)
        return
    }

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

fun Inventory.hasGameItem(item: GameItemStack): Boolean {
    val itemInfo = getItemInfo(item.type) ?: return false
    var itemAmount = item.amount

    for (slot in 0 until size) {
        val current = getGameItem(slot)

        if (current === null)
            continue

        if (current.type !== item.type)
            continue

        if (itemInfo.stackable) {
            itemAmount -= current.amount

            /** If we found enough amount of the given item then we assume that we found it */
            if (itemAmount <= 0)
                return true
        } else {
            /** Found unstackable item that matches the given item */
            return true
        }
    }

    /** The stackable items found total amount is smaller then the given item amount */
    /** Or, we didn't find the unstackable item */
    return false
}

fun Inventory.removeGameItem(item: GameItemStack): Boolean {
    val itemInfo = getItemInfo(item.type) ?: return false
    var itemAmount = item.amount
    val removedSlots = arrayListOf<Int>()

    for (slot in 0 until size) {
        val current = getGameItem(slot)

        if (current === null)
            continue

        if (current.type !== item.type)
            continue

        if (itemInfo.stackable) {
            if (current.amount <= itemAmount) {
                val same = current.amount == itemAmount
                /** We found an item that has the same amount of our item or less */
                itemAmount -= current.amount

                removedSlots.add(slot)

                /** Since the amount counter is zero we can skip going through the other items */
                if (same)
                    break
            } else {
                current.amount -= itemAmount

                removedSlots.forEach { setGameItem(it, null) }
                setGameItem(slot, current)
                return true
            }
        } else {
            /** If we found the unstackable item that equals to the given item we remove it immediately */
            setGameItem(slot, null)
            return true
        }
    }

    if (itemAmount <= 0) {
        removedSlots.forEach { setGameItem(it, null) }

        return true
    }

    /** Unstackable item wasn't found to be removed */
    return false
}
