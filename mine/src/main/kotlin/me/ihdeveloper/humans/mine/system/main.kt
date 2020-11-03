package me.ihdeveloper.humans.mine.system

import me.ihdeveloper.humans.core.Configuration
import me.ihdeveloper.humans.core.GameItemOnBreak
import me.ihdeveloper.humans.core.System
import me.ihdeveloper.humans.core.entity.CustomArmorStand
import me.ihdeveloper.humans.core.item.PrisonCrystal
import me.ihdeveloper.humans.core.registry.getItemInstance
import me.ihdeveloper.humans.core.registry.registerEntity
import me.ihdeveloper.humans.core.util.GameLogger
import me.ihdeveloper.humans.core.util.getGameItem
import me.ihdeveloper.humans.core.util.setGameItem
import me.ihdeveloper.humans.mine.Mine
import me.ihdeveloper.humans.mine.entity.PrisonMineCrystal
import me.ihdeveloper.humans.mine.entity.PrisonMineWizard
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDamageEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin

/**
 * A system for handling entities and registering
 */
class EntitySystem : System("Mine/Entity") {

    override fun init(plugin: JavaPlugin) {
        registerEntity(PrisonMineWizard::class, CustomArmorStand::class, logger)
        registerEntity(PrisonMineCrystal::class, CustomArmorStand::class, logger)
    }

    override fun dispose() {}
}

/**
 * A system for managing and handling mines in the server
 */
class MineSystem : System("Mine"), Listener {
    companion object {
        lateinit var plugin: JavaPlugin
        lateinit var logger: GameLogger

        val mines = mutableListOf<Mine>()

        private val config = Configuration("mines")

        fun save() {
            config.set("mines", arrayListOf<Map<String, Any>>().apply {
                mines.forEach { add(it.serialize()) }
            })
            config.save(logger)
        }
    }

    override fun init(plugin: JavaPlugin) {
        Companion.plugin = plugin
        Companion.logger = logger

        config.load(logger)

        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    override fun lateInit(plugin: JavaPlugin) {
        val rawMines = config.get("mines", arrayListOf<Map<String, Any>>())
        for (rawMine in rawMines) {
            mines.add(Mine.deserialize(rawMine))
        }
    }

    override fun dispose() {}

    @EventHandler(priority = EventPriority.HIGHEST)
    @Suppress("UNUSED")
    fun onInteract(event: PlayerInteractEvent) {
        event.run {
            mines.forEach {
                val table = it.wizard.table
                if (action === Action.RIGHT_CLICK_BLOCK
                    && table.block == clickedBlock
                    && player.inventory.getGameItem(player.inventory.heldItemSlot)?.type === PrisonCrystal::class) {
                    isCancelled = true

                    if (table.size >= 4) {
                        player.sendMessage("§cYou can't place more then 4 crystals in this table")
                        return
                    }

                    logger.debug("Player ${player.name} is placing the ${table.size}th crystal on the wizard table of mine ${it.name}")

                    player.inventory.let { inv ->
                        inv.setGameItem(inv.heldItemSlot, null)
                    }

                    it.wizard.table.add(player)
                    it.broadcastCrystal(player)
                    return
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    @Suppress("UNUSED")
    fun onDamage(event: BlockDamageEvent) {
        event.run {
            mines.forEach {
                if (it.contains(block)) {
                    isCancelled = false
                    return
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    @Suppress("UNUSED")
    fun onBreak(event: BlockBreakEvent) {
        event.run {
            mines.forEach {
                if (it.contains(block)) {
                    val blockType = block.type
                    block.type = Material.BEDROCK

                    if (player.itemInHand.type === Material.AIR) {
                        isCancelled = true
                        return
                    }

                    val gameItemStack = player.inventory.getGameItem(player.inventory.heldItemSlot)

                    if (gameItemStack === null) {
                        isCancelled = true
                        return
                    }

                    if (!gameItemStack.isPickaxe) {
                        logger.debug("The item is not pickaxe!")
                        logger.debug("$gameItemStack")
                        isCancelled = true
                        return
                    }

                    getItemInstance(gameItemStack.type)?.run {
                        if (this is GameItemOnBreak)
                            onBreak(player)
                    }

                    it.onMine(player, blockType)
                    isCancelled = true
                    return
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    @Suppress("UNUSED")
    fun onQuit(event: PlayerQuitEvent) {
        mines.forEach {
            val crystalsCount = it.wizard.table.remove(event.player)
            if (crystalsCount != -1)
                logger.debug("Removing $crystalsCount crystals from the wizard table placed by ${event.player.name} in ${it.name}")

            it.onQuit(event.player)
        }
    }
}
