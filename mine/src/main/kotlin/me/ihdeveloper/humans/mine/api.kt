package me.ihdeveloper.humans.mine

import me.ihdeveloper.humans.core.Command
import me.ihdeveloper.humans.core.SceneMeta
import me.ihdeveloper.humans.core.api.IntegrationAPI
import me.ihdeveloper.humans.core.entity.CustomNPC
import me.ihdeveloper.humans.mine.command.MineCreateCommand
import me.ihdeveloper.humans.mine.command.MineSaveCommand
import me.ihdeveloper.humans.mine.command.MineSetCommand
import me.ihdeveloper.humans.mine.entity.PrisonMineCrystal
import me.ihdeveloper.humans.mine.entity.PrisonMineWizard
import me.ihdeveloper.humans.mine.system.EntitySystem
import me.ihdeveloper.humans.mine.system.MineSystem
import net.minecraft.server.v1_8_R3.Entity
import org.bukkit.Location
import org.bukkit.entity.Player

/**
 * Integrates game's hub with the game's core
 */
class MineIntegrationAPI : IntegrationAPI {
    override val allowNewPlayers = false

    override val commands = arrayOf<Command>(
        MineCreateCommand(),
        MineSetCommand(),
        MineSaveCommand(),
    )

    override val systems = arrayOf(
        EntitySystem(),
        MineSystem(),
    )

    override fun fromEntityType(type: String, location: Location): Entity? = when (type) {
        "prison_mine_wizard" -> PrisonMineWizard(location, PrisonMineWizard.ShopType.UNKNOWN)
        "prison_mine_crystal" -> PrisonMineCrystal(location)
        else -> null
    }

    override fun fromNPCType(type: String, location: Location): CustomNPC? = null

    override fun fromSceneName(name: String): SceneMeta? = null

    override fun playScene(player: Player, name: String): Boolean = false
}
