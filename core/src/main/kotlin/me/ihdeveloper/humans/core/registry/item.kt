package me.ihdeveloper.humans.core.registry

import me.ihdeveloper.humans.core.GameItem
import me.ihdeveloper.humans.core.GameItemInfo
import me.ihdeveloper.humans.core.GameLogger
import org.bukkit.ChatColor
import org.bukkit.inventory.ItemStack
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

private val infos = mutableMapOf<KClass<out GameItem>, GameItemInfo>()
private val instances = mutableMapOf<KClass<out GameItem>, GameItem>()
private val byId = mutableMapOf<String, KClass<out GameItem>>()

fun registerItem(itemClass: KClass<out GameItem>, logger: GameLogger?) {
    var info: GameItemInfo? = null

    for (it in itemClass.annotations) {
        if (it is GameItemInfo)
            info = it
    }

    logger?.debug("Registering item ${itemClass.qualifiedName}...")

    if (info === null) {
        logger?.error("Information about the item class not found!")
        error("GameItemInfo annotation doesn't exist in ${itemClass.qualifiedName}")
    }

    byId[info.id] = itemClass
    infos[itemClass] = info
    instances[itemClass] = itemClass.primaryConstructor!!.call()
}


/**
 * Creates an registered game item
 */
fun createItem(id: String, amount: Int = 1): ItemStack? {
    val itemClass = byId[id]

    if (itemClass === null)
        return null

    return createItem(itemClass, amount)
}

/**
 * Creates an registered game item
 */
fun createItem(itemClass: KClass<out GameItem>, amount: Int = 1): ItemStack {
    val info = infos[itemClass]!!
    val instance = instances[itemClass]!!

    return ItemStack(
        info.material,
        amount,
        info.data
    ).apply {
        itemMeta = itemMeta.apply {
            displayName = "${info.rarity.color}${info.name}"
            lore = mutableListOf<String>().apply {
                addAll(info.description)
                if (info.description.isNotEmpty()) add("§0")
                add("§8-----------------")
                add("${info.rarity.color}${ChatColor.BOLD}${instance.rarityPrefix} ${info.rarity.name}")
            }

            itemFlags.apply {
                clear()
                addAll(info.flags)
            }
        }
    }
}
