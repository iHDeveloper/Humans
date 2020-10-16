package me.ihdeveloper.humans.core.command

import me.ihdeveloper.humans.core.AdminCommand
import me.ihdeveloper.humans.core.scene.IntroScene
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * Command for setting location using key in the scene meta data
 */
class SceneSetLocationCommand : AdminCommand("set-scene-loc") {

    override fun execute(sender: CommandSender?, cmd: Command?, label: String?, args: Array<out String>?): Boolean {
        if (sender !is Player) {
            sender!!.sendMessage("§cYou need to be a player to execute this command!")
            return true
        }

        if (args!!.size < 2) {
            return false
        }

        val name = args[0]
        val key = args[1]

        var failed = false
        when (name) {
            "intro" -> IntroScene.config.set(key, sender.location)
            else -> {
                failed = true
            }
        }

        if (!failed)
            sender.sendMessage("§aSuccessfully! §eSet the location of §6$key§e for §6$name")
        else
            sender.sendMessage("§cThe scene name doesn't exist!")
        return true
    }
}

class SaveSceneCommand : AdminCommand("save-scene") {

    override fun execute(sender: CommandSender?, cmd: Command?, label: String?, args: Array<out String>?): Boolean {
        if (sender !is Player) {
            sender!!.sendMessage("§cYou need to be a player to execute this command!")
            return true
        }

        if (args!!.isEmpty()) {
            return false
        }

        val name = args[0]

        var failed = false
        when (name) {
            "intro" -> IntroScene.save()
            else -> failed = true
        }

        sender.run {
            if (failed) {
                sendMessage("§cFailed! §eCouldn't find the scene's name")
            } else {
                sendMessage("§eSaving scene with name §6$name...")
            }
        }
        return true
    }

}

/**
 * Command for playing and testing a scene in the game
 */
class PlaySceneCommand : AdminCommand("play-scene") {

    override fun execute(sender: CommandSender?, cmd: Command?, label: String?, args: Array<out String>?): Boolean {
        if (sender !is Player) {
            sender!!.sendMessage("§cYou need to be a player to execute this command!")
            return true
        }

        if (args!!.isEmpty()) {
            return false
        }

        val name = args[0]

        var failed = false
        when (name) {
            "intro" -> IntroScene(sender).start()
            else -> failed = true
        }

        sender.run {
            if (failed) {
                sendMessage("§cFailed! §eCouldn't find the scene's name")
            } else {
                sendMessage("§aSuccess! §ePlaying scene §6$name...")
            }
        }
        return true
    }

}
