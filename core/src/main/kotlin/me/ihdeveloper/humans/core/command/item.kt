package me.ihdeveloper.humans.core.command

import java.lang.NumberFormatException
import me.ihdeveloper.humans.core.AdminCommand
import me.ihdeveloper.humans.core.GameItemStack
import me.ihdeveloper.humans.core.registry.NullGameItem
import me.ihdeveloper.humans.core.registry.NullGameItemStack
import me.ihdeveloper.humans.core.util.getNMSItem
import me.ihdeveloper.humans.core.registry.createItem
import me.ihdeveloper.humans.core.registry.getItemClass
import me.ihdeveloper.humans.core.util.setGameItem
import me.ihdeveloper.humans.core.util.setNMSItem
import net.minecraft.server.v1_8_R3.NBTTagCompound
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * Gives game item to the player
 */
class GiveCommand : AdminCommand("give") {
    override fun execute(sender: CommandSender?, cmd: Command?, label: String?, args: Array<out String>?): Boolean {
        if (sender !is Player) {
            sender!!.sendMessage("§cYou have to be a player to execute this command!")
            return true
        }

        if (args!!.isEmpty()) {
            return false
        }

        val id: String = args[0]
        var amount = 1

        if (args.size == 2) {
            try {
                amount = Integer.parseInt(args[0])
            } catch (e: NumberFormatException) {
                sender.sendMessage("§cFailed to parse the amount.")
                return true
            }
        }

        val type = getItemClass(id)

        if (type === null) {
            sender.sendMessage("§cWe couldn't find an item with this ID!")
            return true
        }

        if (sender.itemInHand.type !== Material.AIR) {
            sender.sendMessage("§cYour hand is full with an item!")
            return true
        }

        sender.inventory.apply {
            setGameItem(heldItemSlot, if (type === NullGameItem::class) NullGameItemStack(NBTTagCompound()) else GameItemStack(type, 1))
        }
        return true
    }
}

/**
 * A helper command used to read the item data from NBT
 */
class ItemInfoCommand : AdminCommand("item-info") {

    override fun execute(sender: CommandSender?, cmd: Command?, label: String?, args: Array<out String>?): Boolean {
        if (sender !is Player) {
            sender!!.sendMessage("§cYou have to be a player to execute this command!")
            return true
        }

        if (sender.itemInHand.type === Material.AIR) {
            sender.sendMessage("§cYou have nothing in your hand!")
            return true
        }

        val item = sender.inventory.let { it.getNMSItem(it.heldItemSlot) }

        sender.let { it.sendMessage("§6Printing item info ${it.inventory.itemInHand.itemMeta.displayName}§6...") }

        return item.tag.let {
            val itemData = it.getCompound("ItemData")

            if (itemData == null) {
                sender.sendMessage("§cThe item doesn't have game data!")
                return true
            }

            itemData.apply {
                val id = getString("id")

                sender.printValue("id", id)
            }

            true
        }
    }

    private fun Player.printValue(key: String, value: String?) = sendMessage("§7» §e$key§f: §6${value ?: "§cnull"}")
}