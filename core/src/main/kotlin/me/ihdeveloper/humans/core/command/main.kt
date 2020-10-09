package me.ihdeveloper.humans.core.command

import me.ihdeveloper.humans.core.AdminCommand
import me.ihdeveloper.humans.core.GameLogger
import me.ihdeveloper.humans.core.entity.fromEntityType
import me.ihdeveloper.humans.core.registry.createItem
import me.ihdeveloper.humans.core.registry.spawnEntity
import me.ihdeveloper.humans.core.registry.summonedEntities
import me.ihdeveloper.humans.core.registry.summonedEntitiesInfo
import me.ihdeveloper.humans.core.system.CustomEntitySystem
import me.ihdeveloper.humans.core.system.PlayerSystem
import net.minecraft.server.v1_8_R3.Entity
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import java.lang.NumberFormatException

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
        sender.sendMessage("§aSuccess! §ePlayer spawn has been set! :D")
        return true
    }
}

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
        var amount = 1

        if (args.size == 2) {
            try {
                amount = Integer.parseInt(args[0])
            } catch (e: NumberFormatException) {
                sender.sendMessage("§cFailed to parse the amount.")
                return true
            }
        }

        val item = createItem(id, amount)

        if (item === null) {
            sender.sendMessage("§cWe couldn't find the item with this ID!")
            return true
        }

        sender.inventory.addItem(item)
        return true
    }
}
