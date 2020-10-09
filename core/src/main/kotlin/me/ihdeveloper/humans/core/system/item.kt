package me.ihdeveloper.humans.core.system

import me.ihdeveloper.humans.core.System
import me.ihdeveloper.humans.core.item.PrisonPickaxe
import me.ihdeveloper.humans.core.item.PrisonStone
import me.ihdeveloper.humans.core.registry.registerItem
import org.bukkit.plugin.java.JavaPlugin

class ItemSystem : System("Core/Item") {

    override fun init(plugin: JavaPlugin) {
        /** Prison Natural Items */
        registerItem(PrisonStone::class, logger)

        /** Prison Tool Items */
        registerItem(PrisonPickaxe::class, logger)
    }

    override fun dispose() {}
}
