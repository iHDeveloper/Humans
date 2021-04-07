package me.ihdeveloper.humans.core.util

import com.mojang.authlib.GameProfile
import me.ihdeveloper.humans.core.BossBar
import me.ihdeveloper.humans.core.GameRegion
import me.ihdeveloper.humans.core.core
import me.ihdeveloper.humans.core.entity.connection
import me.ihdeveloper.humans.core.gui.GUIScreen
import me.ihdeveloper.humans.core.system.BossBarSystem
import me.ihdeveloper.humans.core.system.FreezeSystem
import me.ihdeveloper.humans.core.system.GUISystem
import me.ihdeveloper.humans.core.system.ProfileSystem
import me.ihdeveloper.humans.core.system.RegionSystem
import me.ihdeveloper.humans.service.api.Profile
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.chat.ComponentSerializer
import net.minecraft.server.v1_8_R3.IChatBaseComponent
import net.minecraft.server.v1_8_R3.PacketPlayOutChat
import org.bukkit.craftbukkit.v1_8_R3.util.CraftChatMessage
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.SkullMeta

/**
 * Returns the players' game profile
 */
val Player.profile: Profile?
    get() = ProfileSystem.profiles[name]

/**
 * Represents information about the region of the player's location
 */
var Player.region: GameRegion
    set(value) {
        RegionSystem.players[name] = value
    }
    get() = RegionSystem.players[name] ?: RegionSystem.unknown

val Player.gameName: String
    get() = "${scoreboard.getEntryTeam(name).prefix ?: "§7"}$name"

/**
 * Freezes a player's position
 */
fun Player.freeze() {
    FreezeSystem.players[name] = player.location

    // Its broken (I will fix it later)
//    val compound = NBTTagCompound()
//    toNMS().c(compound) // Load data on the NBT compound
//    compound.setByte("NoAI", 1.toByte())
//    toNMS().f(compound) // Save data from the NBT compound
}

/**
 * Unfreezes a player's position. If the player is frozen before.
 */
fun Player.unfreeze() {
    teleport(FreezeSystem.players[player.name])

    // Its broken (I will fix it later)
//    val compound = NBTTagCompound()
//    toNMS().c(compound) // Load data on the NBT compound
//    compound.setByte("NoAI", 0.toByte())
//    toNMS().f(compound) // Save data from the NBT compound

    FreezeSystem.players.remove(name)
}

/**
 * Opens the [GUIScreen] to the player
 */
fun Player.openScreen(screen: GUIScreen) {
    if (GUISystem.screens.containsKey(name)) {
        closeScreen()
    }

    GUISystem.screens[name] = screen
    openInventory(screen)
}

/**
 * Closes the [GUIScreen] from the player
 */
fun Player.closeScreen() = closeInventory()

fun Player.showBossBar(bossBar: BossBar) = BossBarSystem.spawn(bossBar, this)

fun Player.updateBossBar() = BossBarSystem.update(this)

fun Player.hideBossBar() = BossBarSystem.destroy(this)

/**
 * Used to crash the player's connection to avoid "error consequences"
 *
 * The player should report the error code in order for the admins to fix the bug
 */
fun Player.crash(error: String) = kickPlayer(arrayOf(
    "§cConnection crashed! §7($error)",
    "§cSomething wrong happened! §7[${core.serverName}]",
    "§cPlease report the error to the admins.",
).joinToString("\n"))

/**
 * Sends a message to the [Player] in the action bar
 */
fun Player.sendActionBar(message: String) {
    val nmsComponents = IChatBaseComponent.ChatSerializer.a("{\"text\":\"${message}\"}")
    toNMS().connection.sendPacket(PacketPlayOutChat(nmsComponents, 2.toByte()))
}

/**
 * Sets a random game profile with texture data and signature. And, apply it to the skull item
 */
fun SkullMeta.setTexture(data: String, signature: String) {
    val gameProfile = randomGameProfile().apply {
        applyTexture(
            texture = data,
            signature = signature
        )
    }

    this.gameProfile = gameProfile
}

var SkullMeta.gameProfile: GameProfile
    set(value) {
        val field = javaClass.getDeclaredField("profile")
        field.isAccessible = true
        field.set(this, value)
    }
    get() {
        val field = javaClass.getDeclaredField("profile")
        field.isAccessible = true
        return field.get(this) as GameProfile
    }

fun ItemStack.itemMeta(block: ItemMeta.() -> Unit) {
    itemMeta = itemMeta.apply {
        block(this)
    }
}
