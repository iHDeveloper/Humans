package me.ihdeveloper.humans.mine.system

import me.ihdeveloper.humans.core.System
import me.ihdeveloper.humans.core.entity.CustomArmorStand
import me.ihdeveloper.humans.core.registry.registerEntity
import me.ihdeveloper.humans.mine.entity.PrisonMineCrystal
import me.ihdeveloper.humans.mine.entity.PrisonMineWizard
import org.bukkit.plugin.java.JavaPlugin

/*
 * A system that manages mine entities
 */
class EntitySystem : System("Mine/Entity") {

    override fun init(plugin: JavaPlugin) {
        registerEntity(PrisonMineWizard::class, CustomArmorStand::class, logger)
        registerEntity(PrisonMineCrystal::class, CustomArmorStand::class, logger)
    }

    override fun dispose() {}
}
