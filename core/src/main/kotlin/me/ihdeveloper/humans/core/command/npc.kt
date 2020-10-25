package me.ihdeveloper.humans.core.command

import me.ihdeveloper.humans.core.AdminCommand
import me.ihdeveloper.humans.core.system.NPCSystem
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class NPCSummonCommand : AdminCommand("npc-summon") {
    override fun execute(sender: CommandSender?, cmd: Command?, label: String?, args: Array<out String>?): Boolean {
        if (sender !is Player) {
            sender!!.sendMessage("§cYou need to be player to execute this!")
            return true
        }

        if (args!!.isEmpty()) {
            return false
        }

        val type = args[0]
        NPCSystem.add(NPCSystem.NPCInfo(type, sender.location))
        sender.sendMessage("§aSuccess! §eCreated npc with type §b$type")
        return true
    }
}

class NPCSaveCommand : AdminCommand("npc-save") {

    override fun execute(sender: CommandSender?, cmd: Command?, label: String?, args: Array<out String>?): Boolean {
        NPCSystem.save()
        sender!!.sendMessage("§aSuccess! §eSaved the npc configuration!")
        return true
    }
}
