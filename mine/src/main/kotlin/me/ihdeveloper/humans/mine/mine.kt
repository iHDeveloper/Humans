package me.ihdeveloper.humans.mine

import me.ihdeveloper.humans.core.BossBar
import me.ihdeveloper.humans.core.ConfigurationDeserialize
import me.ihdeveloper.humans.core.ConfigurationSerialize
import me.ihdeveloper.humans.core.registry.spawnEntity
import me.ihdeveloper.humans.core.util.GameLogger
import me.ihdeveloper.humans.core.util.hideBossBar
import me.ihdeveloper.humans.core.util.region
import me.ihdeveloper.humans.core.util.showBossBar
import me.ihdeveloper.humans.core.util.updateBossBar
import me.ihdeveloper.humans.mine.entity.PrisonMineWizard
import me.ihdeveloper.humans.mine.system.MineSystem
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player

private const val RESET_TIME = (3 * 60) * 20L

/**
 * Mine is a location where players collect some prison materials.
 * That will help them crafting different items using those materials.
 *
 * When the player breaks one of the mine materials it turns into bedrock immediately.
 *
 *
 */
class Mine(
    val name: String,
    private val regionName: String,
    private val pos1: Location,
    private val pos2: Location,
    private val wizardSpawn: Location,
    private val blocks: List<Material>,
) : Runnable, ConfigurationSerialize {
    companion object: ConfigurationDeserialize<Mine> {
        override fun deserialize(data: Map<String, Any>) = Mine(
            name = data["name"] as String,
            regionName = data["regionName"] as String,
            pos1 = data["pos1"] as Location,
            pos2 = data["pos2"] as Location,
            blocks = data["blocks"].run {
                mutableListOf<Material>().let {
                    if (this is ArrayList<*>) {
                        for (rawBlock in this) {
                            if (rawBlock !is String)
                                continue

                            it.add(Material.valueOf(rawBlock.toUpperCase()))
                        }
                    }

                    it.toList()
                }
            },
            wizardSpawn = data["wizardSpawn"] as Location,
        )
    }

    private val logger = GameLogger("Mine/$name")
    private val bossBar = BossBar("§eReset Time:§f 00:00 §7§l| §eCrystals §7[§c0§7/§a4§7]")

    /** A map of the count of mined blocks by the player */
    private val minersCount = mutableMapOf<String, Int>()

    private val players = mutableSetOf<String>()

    private val wizard = PrisonMineWizard(wizardSpawn)

    private var resetTime = RESET_TIME

    private var blocksSize = 0
    private var minedBlocks = 0

    init {
        logger.debug("Initializing...")
        blocks.forEach { logger.debug("Loaded block: $it") }
        spawnEntity(wizard, false, logger)

        reset()

        logger.debug("Mine Blocks Size: $blocksSize")
        Bukkit.getScheduler().runTaskTimer(MineSystem.plugin, this, 0L, 1L)
    }

    override fun run() {
        if (minedBlocks == 0) {
            resetTime = RESET_TIME
        }

        bossBar.title = "§eReset Time:§f ${resetToString()} §7§l| §eCrystals §7[§c0§7/§a4§7]"
        bossBar.current = minedBlocks
        bossBar.max = blocksSize

        for (player in pos1.world.players) {
            if (player.region.name != regionName && player.region.name != "$regionName-wizard") {
                if (players.contains(player.name)) {
                    player.hideBossBar()
                    players.remove(player.name)
                }
                continue
            }

            if (!players.contains(player.name)) {
                player.showBossBar(bossBar)
                players.add(player.name)
            } else {
                player.updateBossBar()
            }
        }

        resetTime--
    }

    override fun serialize() = mapOf(
        "name" to name,
        "regionName" to regionName,
        "pos1" to pos1,
        "pos2" to pos2,
        "blocks" to blocks,
        "wizardSpawn" to wizardSpawn,
    )

    fun contains(block: Block): Boolean = false

    fun onMine(player: Player) {
        minedBlocks++

        val count = minersCount[player.name] ?: 0
        minersCount[player.name] = count + 1
    }

    fun onQuit(player: Player) {
        if (!players.contains(player.name))
            return

        player.hideBossBar()
        players.remove(player.name)
    }

    private fun reset() {
        minersCount.clear()

        calculateBlocks()
    }

    private fun calculateBlocks() {
        blocksSize = 0

        for (y in pos1.blockY..pos2.blockY) {
            for (x in pos2.blockX..pos1.blockX) {
                for (z in pos1.blockZ..pos2.blockZ) {
                    pos1.world.run {
                        val type = getBlockAt(x, y, z).type

                        if (type !== Material.AIR) {
                            for (blockType in blocks) {
                                if (blockType === type) {
                                    blocksSize++
                                    break
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun resetToString(): String {
        var seconds = resetTime / 20
        var minutes = seconds / 60

        seconds %= 60
        minutes %= 60

        return "${if(minutes <= 9) "0$minutes" else minutes}:${if(seconds <= 9) "0$seconds" else seconds}"
    }
}