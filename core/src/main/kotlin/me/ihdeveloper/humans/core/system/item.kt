package me.ihdeveloper.humans.core.system

import me.ihdeveloper.humans.core.System
import me.ihdeveloper.humans.core.item.PrisonCoalPass
import me.ihdeveloper.humans.core.item.PrisonCrystal
import me.ihdeveloper.humans.core.item.PrisonCursedPickaxe
import me.ihdeveloper.humans.core.item.PrisonUltimatePickaxe
import me.ihdeveloper.humans.core.item.PrisonEnchantedStone
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
        registerItem(PrisonEnchantedStone::class, logger)

        /** Prison Tool Items */
        registerItem(PrisonNormalPickaxe::class, logger)
        registerItem(PrisonCursedPickaxe::class, logger)
        registerItem(PrisonUltimatePickaxe::class, logger)

        /** Prison Mine Items */
        registerItem(PrisonCrystal::class, logger)
        registerItem(PrisonCoalPass::class, logger)
    }

    override fun dispose() {}
}
