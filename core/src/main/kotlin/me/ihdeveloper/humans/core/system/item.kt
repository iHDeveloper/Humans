package me.ihdeveloper.humans.core.system

import me.ihdeveloper.humans.core.System
import me.ihdeveloper.humans.core.item.PrisonCursedPickaxe
import me.ihdeveloper.humans.core.item.PrisonNormalPickaxe
import me.ihdeveloper.humans.core.item.PrisonStone
import me.ihdeveloper.humans.core.registry.NullGameItem
import me.ihdeveloper.humans.core.registry.registerItem
import org.bukkit.plugin.java.JavaPlugin

class ItemSystem : System("Core/Item") {

    override fun init(plugin: JavaPlugin) {
        registerItem(NullGameItem::class, logger)

        /** Prison Natural Items */
        registerItem(PrisonStone::class, logger)

        /** Prison Tool Items */
        registerItem(PrisonNormalPickaxe::class, logger)
        registerItem(PrisonCursedPickaxe::class, logger)
    }

    override fun dispose() {}
}
