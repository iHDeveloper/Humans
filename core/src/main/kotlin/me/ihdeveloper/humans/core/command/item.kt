package me.ihdeveloper.humans.core.command

import java.lang.NumberFormatException
import me.ihdeveloper.humans.core.AdminCommand
import me.ihdeveloper.humans.core.GameItemStack
import me.ihdeveloper.humans.core.registry.NullGameItem
import me.ihdeveloper.humans.core.registry.NullGameItemStack
import me.ihdeveloper.humans.core.util.getNMSItem
import me.ihdeveloper.humans.core.registry.createItem
import me.ihdeveloper.humans.core.registry.getItemClass
import me.ihdeveloper.humans.core.registry.getItemInfo
import me.ihdeveloper.humans.core.util.addGameItem
import me.ihdeveloper.humans.core.util.setGameItem
import me.ihdeveloper.humans.core.util.setNMSItem
import net.minecraft.server.v1_8_R3.NBTTagCompound
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

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
        var name: String = sender.name
        var amount = 1

        if (args.size >= 3) {
            name = args[1]

            try {
                amount = Integer.parseInt(args[1])
            } catch (e: NumberFormatException) {
                sender.sendMessage("§cFailed to parse the amount.")
                return true
            }
        } else if (args.size == 2) {
            try {
                amount = Integer.parseInt(args[1])
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

        val player = Bukkit.getPlayerExact(name)

        if (player == null) {
            sender.sendMessage("§cFailed! §ePlayer not found")
            return true
        }

        player.inventory.apply {
            if (!addGameItem(
                if (type === NullGameItem::class)
                    NullGameItemStack(NBTTagCompound().apply {
                        CraftItemStack.asNMSCopy(ItemStack(Material.STONE, 1, 2.toShort())).save(this)
                    })
                else
                    GameItemStack(type, amount)
            )) {
                sender.sendMessage("§cYour inventory is full to collect the mined item!")
                return true
            }
            sender.sendMessage("§aSuccess! §eYou got ${getItemInfo(type)?.name ?: "§7NULL"}")
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

        val item = sender.inventory.let { it.getNMSItem(it.heldItemSlot) }

        if (item === null) {
            sender.sendMessage("§cYou have nothing in your hand!")
            return true
        }

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
