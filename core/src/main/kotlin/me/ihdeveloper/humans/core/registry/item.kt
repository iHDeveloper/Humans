package me.ihdeveloper.humans.core.registry

import me.ihdeveloper.humans.core.GameItem
import me.ihdeveloper.humans.core.GameItemInfo
import me.ihdeveloper.humans.core.util.GameLogger
import org.bukkit.ChatColor
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor
import me.ihdeveloper.humans.core.GameItemRarity
import me.ihdeveloper.humans.core.GameItemStack
import me.ihdeveloper.humans.core.GameItemTexture
import me.ihdeveloper.humans.core.GameItemWithTexture
import me.ihdeveloper.humans.core.item.EnchantedNaturalItem
import me.ihdeveloper.humans.core.util.NMSItemStack
import me.ihdeveloper.humans.core.util.applyTexture
import me.ihdeveloper.humans.core.util.gameProfile
import me.ihdeveloper.humans.core.util.itemMeta
import me.ihdeveloper.humans.core.util.randomGameProfile
import net.minecraft.server.v1_8_R3.NBTTagCompound
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.meta.SkullMeta

private val infos = mutableMapOf<KClass<out GameItem>, GameItemInfo>()
private val instances = mutableMapOf<KClass<out GameItem>, GameItem>()
private val byId = mutableMapOf<String, KClass<out GameItem>>()

@GameItemInfo(
    id = "null",
    name = "null",
    description = [
        "§7Contains information about",
        "§7unknown item.",
        "§0",
        "§7Please report this item",
        "§7to §eAgent H"
    ],
    material = Material.STONE,
    data = Short.MAX_VALUE,
    rarity = GameItemRarity.SPECIAL,
)
class NullGameItem : GameItem()

open class NullGameItemStack(
    val nbt: NBTTagCompound
): GameItemStack(NullGameItem::class)

/**
 * Returns a game item stateless instance class
 */
fun getItemClass(id: String): KClass<out GameItem>? = byId[id]

/**
 * Returns a game item instance
 */
fun getItemInstance(type: KClass<out GameItem>): GameItem? = instances[type]

/**
 * Returns information about the game item
 */
fun getItemInfo(type: KClass<out GameItem>): GameItemInfo? = infos[type]

/**
 * Register a game item in the item registry
 */
fun registerItem(itemClass: KClass<out GameItem>, logger: GameLogger?) {
    var info: GameItemInfo? = null
    var texture: GameItemTexture? = null

    for (it in itemClass.annotations) {
        if (it is GameItemInfo)
            info = it
        else if (it is GameItemTexture)
            texture = it
    }

    logger?.debug("Registering item ${itemClass.qualifiedName}...")

    if (info === null) {
        logger?.error("Information about the item class not found!")
        error("GameItemInfo annotation doesn't exist in ${itemClass.qualifiedName}")
    }

    byId[info.id] = itemClass
    infos[itemClass] = info
    instances[itemClass] = if (texture == null) {
        itemClass.primaryConstructor!!.call()
    } else {
        itemClass.primaryConstructor!!.call(randomGameProfile().apply {
            applyTexture(
                texture.texture,
                texture.signature
            )
        })
    }
}


/**
 * Creates a game item from the item registry
 */
fun createItem(id: String, amount: Int = 1): NMSItemStack? {
    val itemClass = byId[id]

    if (itemClass === null)
        return null

    return createItem(itemClass, amount)
}

/**
 * Creates an registered game item
 */
fun createItem(itemClass: KClass<out GameItem>, amount: Int = 1): NMSItemStack {
    val info = infos[itemClass]!!
    val instance = instances[itemClass]!!

    val bukkitItem = ItemStack(info.material, amount, info.data)

    if (instance is GameItemWithTexture) {
        bukkitItem.apply {
            itemMeta {
                (this as SkullMeta).apply {
                    gameProfile = instance.gameProfile
                }
            }
        }
    }

    bukkitItem.apply {
        itemMeta {
            displayName = "${info.rarity.color}${info.name}"
            lore = mutableListOf<String>().apply {
                addAll(info.description)
                if (info.description.isNotEmpty()) add("§0")
                add("§8-----------------")
                add("${info.rarity.color}${ChatColor.BOLD}${info.rarity.name} ${instance.raritySuffix ?: ""}")
            }

            info.flags.forEach { if (it != ItemFlag.HIDE_UNBREAKABLE) addItemFlags(it) }

            if (instance is EnchantedNaturalItem) {
                addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true)
                addItemFlags(ItemFlag.HIDE_ENCHANTS)
            }

            if (info.unbreakable) {
                addItemFlags(ItemFlag.HIDE_UNBREAKABLE)
                spigot().isUnbreakable = true
            }
        }
    }

    return CraftItemStack.asNMSCopy(bukkitItem).apply {
        tag.set("ItemData", info.toNBT())
    }
}

/**
 * Converts the [GameItemInfo] into [NBTTagCompound]
 */
private fun GameItemInfo.toNBT() = NBTTagCompound().apply {
    setString("id", id)
}
