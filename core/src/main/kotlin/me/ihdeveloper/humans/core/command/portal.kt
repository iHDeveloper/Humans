package me.ihdeveloper.humans.core.command

import me.ihdeveloper.humans.core.AdminCommand
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

var portalState = false

/**
 * Locks the players from accessing the humans portal
 */
class PortalLockCommand : AdminCommand("portal-lock") {
    override fun execute(sender: CommandSender?, cmd: Command?, label: String?, args: Array<out String>?): Boolean {
        if (portalState) {
            sender!!.sendMessage("§cPortal is already locked!")
            return true
        }
        portalState = true
        sender!!.sendMessage("§cPortal is locked now! §ePlayers can't access the Humans portal")
        return true
    }
}

/**
 * Unlocks the players from accessing the humans portal
 */
class PortalUnlockCommand : AdminCommand("portal-unlock") {
    override fun execute(sender: CommandSender?, cmd: Command?, label: String?, args: Array<out String>?): Boolean {
        if (!portalState) {
            sender!!.sendMessage("§cPortal is already unlocked!")
            return true
        }
        portalState = false
        sender!!.sendMessage("§cPortal is now unlocked! §ePlayers can access the Humans portal")
        return true
    }
}
