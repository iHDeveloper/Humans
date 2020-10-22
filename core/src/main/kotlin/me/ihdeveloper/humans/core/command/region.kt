package me.ihdeveloper.humans.core.command

import me.ihdeveloper.humans.core.Command
import me.ihdeveloper.humans.core.GameRegion
import me.ihdeveloper.humans.core.system.RegionSystem
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

private var regionName: String? = null
private var displayName: String? = null
private var from: Location? = null
private var to: Location? = null

/**
 * Creates a new instance of the game region with the provided information from [RegionSetCommand]
 */
class RegionNewCommand : Command("region-new") {
    override fun execute(
        sender: CommandSender?,
        cmd: org.bukkit.command.Command?,
        label: String?,
        args: Array<out String>?
    ): Boolean {
        when {
            regionName == null -> {
                sender!!.sendMessage("§cNo name found for the region!")
                return true
            }
            displayName == null -> {
                sender!!.sendMessage("§cNo display name found for the region!")
                return true
            }
            from == null -> {
                sender!!.sendMessage("§cNo from[Location] found for the region!")
                return true
            }
            to == null -> {
                sender!!.sendMessage("§cNo to[Location] found for the region!")
                return true
            }
            else -> {
                RegionSystem.regions.add(GameRegion(regionName!!, displayName!!, from!!, to!!))
                sender!!.sendMessage("§aSuccess! §eCreated region §9${regionName ?: "§7unknown"}")
                return true
            }
        }

    }
}

/**
 * Sets information about the region
 */
class RegionSetCommand : Command("region-set") {
    override fun execute(
        sender: CommandSender?,
        cmd: org.bukkit.command.Command?,
        label: String?,
        args: Array<out String>?
    ): Boolean {
        if (args!!.isEmpty())
            return false

        val type = args[0]

        if (args.size >= 2) {
            var value = args[1]
            if (args.size > 2) for (i in 2 until args.size) {
                value += " " + args[i]
            }

            when(type) {
                "name" -> regionName = value
                "displayName" -> displayName = value
                else -> return false
            }

            sender!!.sendMessage("§aSuccess! §eSet value of §9$type§e for §9${regionName ?: "§7unknown"}§e with§r $value")
            return true
        }

        if (sender !is Player) {
            sender!!.sendMessage("§cYou have to be a player to execute this command!")
            return true
        }

        when (type) {
            "from" -> from = sender.location
            "to" -> to = sender.location
            else -> return false
        }

        sender.sendMessage("§aSuccess! §eSet location of §9$type§e for §9$regionName")
        return true
    }
}

/**
 * Saves the region configuration
 */
class RegionSaveCommand : Command("region-save") {

    override fun execute(
        sender: CommandSender?,
        cmd: org.bukkit.command.Command?,
        label: String?,
        args: Array<out String>?
    ): Boolean {
        RegionSystem.run {
            save()
            sender!!.sendMessage("§aSuccess! §eSaved the regions configuration with §6${regions.size}§e regions.")
            return true
        }
    }
}
