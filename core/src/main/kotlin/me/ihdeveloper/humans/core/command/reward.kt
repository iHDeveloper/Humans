package me.ihdeveloper.humans.core.command

import me.ihdeveloper.humans.core.AdminCommand
import me.ihdeveloper.humans.core.GameItemStack
import me.ihdeveloper.humans.core.entity.RewardSquare
import me.ihdeveloper.humans.core.item.PrisonEnchantedStone
import me.ihdeveloper.humans.core.item.PrisonStone
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

private var square: RewardSquare? = null

class RewardCreateCommand : AdminCommand("reward-create") {
    override fun execute(sender: CommandSender?, cmd: Command?, label: String?, args: Array<out String>?): Boolean {
        if (sender !is Player) {
            sender!!.sendMessage("§cYou have to be a player to execute this command")
            return true
        }

        if (square != null) {
            sender.sendMessage("§cFailed!§e Reward square exists. §7Try /reward-destroy")
            return true
        }

        square = RewardSquare(
            sender.location.block.location,
            15,
            3,
            2,
            ItemStack(Material.STONE_PICKAXE),
            arrayOf(
                GameItemStack(PrisonStone::class, 1),
                GameItemStack(PrisonEnchantedStone::class, 1),
                GameItemStack(PrisonStone::class, 1),
                GameItemStack(PrisonStone::class, 1),
            )
        )
        sender.sendMessage("§aSuccess!§e Reward created!")
        return true
    }
}

class RewardSpawnCommand : AdminCommand("reward-spawn") {
    override fun execute(sender: CommandSender?, cmd: Command?, label: String?, args: Array<out String>?): Boolean {
        if (square == null) {
            sender!!.sendMessage("§cFailed!§e Reward square hasn't been created.")
        } else {
            square!!.spawn()
            sender!!.sendMessage("§aSuccess!§e Reward square spawned.")
        }
        return true
    }
}

class RewardDestroyCommand : AdminCommand("reward-destroy") {
    override fun execute(sender: CommandSender?, cmd: Command?, label: String?, args: Array<out String>?): Boolean {
        if (square != null) {
            square!!.destroy()
            square = null
            sender!!.sendMessage("§aSuccess!§e Reward square destroyed.")
        } else {
            sender!!.sendMessage("§cFailed!§e Reward square hasn't been created.")
        }
        return true
    }
}
