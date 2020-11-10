package me.ihdeveloper.humans.mine.command

import me.ihdeveloper.humans.core.AdminCommand
import me.ihdeveloper.humans.mine.Mine
import me.ihdeveloper.humans.mine.system.MineSystem
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

private var mineName: String? = null
private var regionName: String? = null
private var pos1: Location? = null
private var pos2: Location? = null
private var wizardSpawn: Location? = null
private var rewardSpawn: Location? = null
private var blocks: MutableList<Material> = mutableListOf(Material.STONE)

/**
 * A command for generating the mine after setting the data
 */
class MineCreateCommand : AdminCommand("mine-create") {
    override fun execute(sender: CommandSender?, cmd: Command?, label: String?, args: Array<out String>?): Boolean {
        when {
            mineName === null -> {
                sender!!.sendMessage("§cFailed! §eThe name is not set!")
            }
            regionName === null -> {
                sender!!.sendMessage("§cFailed! §eThe region name is not set!")
            }
            pos1 === null -> {
                sender!!.sendMessage("§cFailed! §eThe pos1 is not set!")
            }
            pos2 === null -> {
                sender!!.sendMessage("§cFailed! §eThe pos2 is not set!")
            }
            wizardSpawn === null -> {
                sender!!.sendMessage("§cFailed! §eThe wizard spawn is not set!")
            }
            rewardSpawn === null -> {
                sender!!.sendMessage("§cFailed! §eThe reward spawn is not set!")
            }
            else -> {
                MineSystem.mines.add(Mine(
                    mineName!!,
                    regionName!!,
                    pos1!!,
                    pos2!!,
                    wizardSpawn!!,
                    rewardSpawn!!,
                    blocks,
                ))
            }
        }
        return true
    }
}

/**
 * A command for setting data for the mine to be generated
 */
class MineSetCommand : AdminCommand("mine-set") {
    override fun execute(sender: CommandSender?, cmd: Command?, label: String?, args: Array<out String>?): Boolean {
        if (args!!.isEmpty())
            return false

        if (args.size >= 2) {
            val type = args[0]
            val value = args[1]

            var failed = false
            when (type) {
                "name" -> {
                    mineName = value
                }
                "region" -> {
                    regionName = value
                }
                else -> {
                    failed = true
                }
            }

            if (failed) {
                sender!!.sendMessage("§cFailed! §eThe type §9$type§e is not found")
            } else {
                sender!!.sendMessage("§aSuccess! §eSet value of §9$type§e as §9$value§e in §9${mineName ?: "§7Unknown"}")
            }
            return true
        }

        if (sender !is Player) {
            sender!!.sendMessage("§cYou have to be a player to execute this command!")
            return true
        }

        val type = args[0]

        var failed = false
        when (type) {
            "pos1" -> {
                pos1 = sender.location
            }
            "pos2" -> {
                pos2 = sender.location
            }
            "wizard" -> {
                wizardSpawn = sender.location
            }
            "reward" -> {
                rewardSpawn = sender.location
            }
            else -> {
                failed = true
            }
        }

        if (failed) {
            sender.sendMessage("§cFailed! §eThe type §9$type§e is not found")
        } else {
            sender.sendMessage("§aSuccess! §eSet location of §9$type§e §e in §9${mineName ?: "§7Unknown"}")
        }
        return true
    }
}

/**
 * A command to save the mines configuration
 */
class MineSaveCommand : AdminCommand("mine-save") {
    override fun execute(sender: CommandSender?, cmd: Command?, label: String?, args: Array<out String>?): Boolean {
        MineSystem.save()
        sender!!.sendMessage("§aSuccess! §eSaved the mines configuration")
        return true
    }
}
