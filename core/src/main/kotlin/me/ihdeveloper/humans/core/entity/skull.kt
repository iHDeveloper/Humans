package me.ihdeveloper.humans.core.entity

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack
import org.bukkit.inventory.ItemStack

/**
 * A big wither skull entity.
 *
 * It represents how powerful and strong the Wither King is.
 */
class WitherSkull(
    location: Location
) : CustomGiant(location) {

    init {
        initLocation()
        isInvisible = true

        equipment[1] = CraftItemStack.asNMSCopy(ItemStack(Material.SKULL_ITEM, 1, 1.toShort()))
    }

}
