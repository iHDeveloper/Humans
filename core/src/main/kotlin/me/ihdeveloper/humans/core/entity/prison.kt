package me.ihdeveloper.humans.core.entity

import net.minecraft.server.v1_8_R3.ItemStack
import net.minecraft.server.v1_8_R3.Items
import org.bukkit.Location

class PrisonGuard(location: Location)
    : CustomSkeleton(location) {

    init {
        setLocation()

        clearPathfinderSelector(goalSelector)
        clearPathfinderSelector(targetSelector)

        setEquipment(0, ItemStack(Items.IRON_SWORD))
        setEquipment(2, ItemStack(Items.LEATHER_CHESTPLATE))
        setEquipment(3, ItemStack(Items.LEATHER_LEGGINGS))

        nameHologram.text = "§cPrison Guard"
        spawnHologram()
    }

}
