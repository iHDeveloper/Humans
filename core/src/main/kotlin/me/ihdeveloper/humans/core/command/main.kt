package me.ihdeveloper.humans.core.command

import me.ihdeveloper.humans.core.AdminCommand
import me.ihdeveloper.humans.core.util.GameLogger
import me.ihdeveloper.humans.core.entity.fromEntityType
import me.ihdeveloper.humans.core.registry.spawnEntity
import me.ihdeveloper.humans.core.registry.summonedEntities
import me.ihdeveloper.humans.core.registry.summonedEntitiesInfo
import me.ihdeveloper.humans.core.system.CustomEntitySystem
import me.ihdeveloper.humans.core.system.PlayerSystem
import me.ihdeveloper.humans.core.util.crash
import net.minecraft.server.v1_8_R3.Entity
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player

class SummonCommand : AdminCommand("summon") {
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

        entity = fromEntityType(type, location)

        if (entity == null) {
            player.sendMessage("§cFailed! §eCouldn't find the entity name.")
            return true
        }

        val result = spawnEntity(entity, false, logger)
        if (result) {
            summonedEntities.add(entity)
            summonedEntitiesInfo.add(CustomEntitySystem.EntityInfo(type, location))
            player.sendMessage("§aSuccess! §eSpawned an entity with type §b$type")
        } else {
            player.sendMessage("§cFailed! §eUnable to spawn entity with type §b$type")
        }
        return true
    }
}

class SummonSaveCommand : AdminCommand("summon-save") {

    override fun execute(sender: CommandSender?, cmd: Command?, label: String?, args: Array<out String>?): Boolean {
        CustomEntitySystem.save()
        sender!!.sendMessage("§aSuccess! §eSaved the entities configuration!")
        return true
    }
}

class SetSpawnCommand : AdminCommand("set-spawn") {
    override fun execute(sender: CommandSender?, cmd: Command?, label: String?, args: Array<out String>?): Boolean {
        if (sender !is Player) {
            sender!!.sendMessage("§cThis command requires a player to be executed!")
            return true
        }

        PlayerSystem.spawn = sender.location
        PlayerSystem.save()
        sender.sendMessage("§aSuccess! §ePlayer spawn has been set! :D")
        return true
    }
}

class CrashCommand : AdminCommand("crash") {
    override fun execute(sender: CommandSender?, cmd: Command?, label: String?, args: Array<out String>?): Boolean {
        if (sender !is Player) {
            sender!!.sendMessage("You have to be a player to execute this command!")
            return true
        }

        sender.crash("NO_REASON_AT_ALL")
        return true
    }
}
