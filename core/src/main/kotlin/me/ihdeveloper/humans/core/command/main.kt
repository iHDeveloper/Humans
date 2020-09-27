package me.ihdeveloper.humans.core.command

import me.ihdeveloper.humans.core.Command
import me.ihdeveloper.humans.core.GameLogger
import me.ihdeveloper.humans.core.entity.Hologram
import me.ihdeveloper.humans.core.entity.PrisonGuard
import me.ihdeveloper.humans.core.spawnEntity
import net.minecraft.server.v1_8_R3.Entity
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player

class SummonCommand : Command("summon") {
    private val logger = GameLogger("Core/Command/Summon")

    override fun execute(sender: CommandSender?, cmd: org.bukkit.command.Command?, label: String?, args: Array<out String>?): Boolean {
        if (sender is ConsoleCommandSender) {
            sender.sendMessage("§cThis command requires a player to execute it")
            return true
        }

        if (args?.size!! < 1)
            return false

        val player = sender as Player

        if (!player.isOp) {
            player.sendMessage("§c${cmd!!.permissionMessage}")
            return true
        }

        val location = player.location
        val type = args[0]

        val entity: Entity?

        entity = when(type) {
            "prison_guard" -> PrisonGuard(location)
            "hologram" -> Hologram(location, "Text")
            else -> null
        }

        if (entity == null) {
            player.sendMessage("§cFailed! §eCouldn't find the entity name.")
            return true
        }

        val result = spawnEntity(entity, false, logger)
        if (result) {
            player.sendMessage("§aSuccess! §eSpawned an entity with type §b$type")
        } else {
            player.sendMessage("§cFailed! §eUnable to spawn entity with type §b$type")
        }
        return true
    }

}
