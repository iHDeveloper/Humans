package me.ihdeveloper.humans.core.command

import me.ihdeveloper.humans.core.AdminCommand
import me.ihdeveloper.humans.core.Warp
import me.ihdeveloper.humans.core.WarpInfo
import me.ihdeveloper.humans.core.warps
import me.ihdeveloper.humans.core.warpsInfo
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

var instance: WarpInfo? = null
var displayName: String? = null
var center: Location? = null
var start: Location? = null
var end: Location? = null

class CreateWarpCommand : AdminCommand("create-warp") {

    override fun execute(sender: CommandSender?, cmd: Command?, label: String?, args: Array<out String>?): Boolean {
        if (displayName == null) {
            sender!!.sendMessage("§cThe display name is null")
            return true
        }

        if (center == null) {
            sender!!.sendMessage("§cThe center location is null")
            return true
        }

        if (start == null) {
            sender!!.sendMessage("§cThe start location is null")
            return true
        }

        if (end == null) {
            sender!!.sendMessage("§cThe end location is null")
            return true
        }

        instance = WarpInfo(
            displayName = displayName!!,
            center = center!!,
            start = start!!,
            end = end!!
        )
        displayName = null
        center = null
        start = null
        end = null

        warpsInfo.add(instance!!)
        Warp.fromInfo(instance!!).run {
            init()
            warps.add(this)
        }
        return true
    }
}

class SetWarpDisplayNameCommand : AdminCommand("set-warp-name") {

    override fun execute(sender: CommandSender?, cmd: Command?, label: String?, args: Array<out String>?): Boolean {
        if (args!!.isEmpty())
            return false

        displayName = args[0]

        sender!!.sendMessage("§aSuccess! §eSet the display name of the warp.")

        if (args.size < 2)
            return true

        for (i in 1 until args.size) {
            displayName += " " + args[i]
        }
        return true
    }
}

class SetWarpLocationCommand : AdminCommand("set-warp-loc") {

    override fun execute(sender: CommandSender?, cmd: Command?, label: String?, args: Array<out String>?): Boolean {
        if (args!!.isEmpty())
            return false

        if (sender !is Player) {
            sender!!.sendMessage("§cThis command is only executed by the player!")
            return true
        }

        val type = args[0]
        val location = sender.location.block.location.apply {
            if (type != "center")
                add(0.5, 0.0, 0.5)
            yaw = sender.location.yaw
            pitch = sender.location.pitch
        }

        when (type) {
            "center" -> center = location
            "start" -> start = location
            "end" -> end = location
            else -> {
                sender.sendMessage("§cFailed! §eThe type §c$type §eis unknown to the warp info.")
                return true
            }
        }

        sender.sendMessage("§aSuccess! §eThe warp location of §b$type §eis set!")
        return true
    }
}
