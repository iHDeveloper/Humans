package me.ihdeveloper.humans.core

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import net.minecraft.server.v1_8_R3.EntityPlayer
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*

/**
 * An instance of Item Stack AIR
 * Without allocating more of them
 */
val ITEMSTACK_AIR = ItemStack(Material.AIR)

/** Minecraft Colors */
val COLOR_AQUA = ChatColor.AQUA
val COLOR_CYAN = ChatColor.DARK_AQUA
val COLOR_RED = ChatColor.RED
val COLOR_GOLD = ChatColor.GOLD
val COLOR_GRAY = ChatColor.GRAY
val COLOR_RESET = ChatColor.RESET
val COLOR_YELLOW = ChatColor.YELLOW
val COLOR_WHITE = ChatColor.WHITE

/**
 * An utility to help in logging the game actions
 */
class GameLogger(private val name: String) {

    /**
     * Prints an info message to the logger
     */
    fun info(message: String) = print(COLOR_AQUA, "INFO", message)

    /**
     * Prints a warning message to the logger
     */
    fun warn(message: String) = print(COLOR_YELLOW, "WARN", message)

    /**
     * Prints an error message to the logger
     */
    fun error(message: String) = print(COLOR_RED, "ERR", message)

    /**
     * Prints a debug message to the logger
     */
    fun debug(message: String) = print(COLOR_GOLD, "DEBUG", message)

    private fun print(color: ChatColor, prefix: String, message: String) = Bukkit.getConsoleSender().sendMessage(
        "${COLOR_GRAY}[" +
        "${COLOR_GOLD}Humans" +
        "${COLOR_GRAY}:" +
        "$COLOR_CYAN$name" +
        "${COLOR_GRAY}:" +
        "$COLOR_RESET$color$prefix" +
        "${COLOR_GRAY}] " +
        "$COLOR_RESET$color$message"
    )
}

/**
 * Convert Bukkit [World] to Minecraft [net.minecraft.server.v1_8_R3.World]
 */
fun toNMSWorld(bukkitWorld: World): net.minecraft.server.v1_8_R3.World = (bukkitWorld as CraftWorld).handle

fun toNMSPlayer(player: Player): EntityPlayer = (player as CraftPlayer).handle

/**
 * Check if the location is between two locations
 */
fun Location.between(from: Location, to: Location): Boolean {
    if (from.y > y || y > to.y)
        return false
    if (from.x < x || x < to.x)
        return false
    if (from.z > z || z > to.z)
        return false
    return true
}

fun randomGameProfile(): GameProfile {
    val uuid = UUID.randomUUID()
    return GameProfile(uuid, uuid.toString().substring(0, 8))
}

/**
 * Apply a texture to the [GameProfile]
 */
fun GameProfile.applyTexture(texture: String, signature: String) {
    properties.put("textures", Property("textures", texture, signature))
}