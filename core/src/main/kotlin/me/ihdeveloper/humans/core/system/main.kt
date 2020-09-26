package me.ihdeveloper.humans.core.system

import me.ihdeveloper.humans.core.System
import me.ihdeveloper.humans.core.entity.CustomArmorStand
import me.ihdeveloper.humans.core.entity.Hologram
import me.ihdeveloper.humans.core.overrideEntity
import me.ihdeveloper.humans.core.registerEntity
import net.minecraft.server.v1_8_R3.EntityArmorStand
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

/**
 * A system for registering the entities of the game core
 */
class CustomEntitySystem : System("Core/Custom-Entity"), Listener {

    override fun init(plugin: JavaPlugin) {
        logger.info("Overriding entities...")

        /** Override base entities with custom ones */
        overrideEntity(EntityArmorStand::class, CustomArmorStand::class, logger)

        logger.info("Registering entities...")

        /** Register custom entities */
        registerEntity(Hologram::class, CustomArmorStand::class, logger)
    }

    override fun dispose() {}

}
