package me.ihdeveloper.humans.mine

import kotlin.random.Random
import me.ihdeveloper.humans.core.BossBar
import me.ihdeveloper.humans.core.ConfigurationDeserialize
import me.ihdeveloper.humans.core.ConfigurationSerialize
import me.ihdeveloper.humans.core.GameItemStack
import me.ihdeveloper.humans.core.Scene
import me.ihdeveloper.humans.core.SceneState
import me.ihdeveloper.humans.core.item.PrisonEnchantedStone
import me.ihdeveloper.humans.core.item.PrisonStone
import me.ihdeveloper.humans.core.registry.spawnEntity
import me.ihdeveloper.humans.core.util.GameLogger
import me.ihdeveloper.humans.core.util.addGameItem
import me.ihdeveloper.humans.core.util.crash
import me.ihdeveloper.humans.core.util.gameName
import me.ihdeveloper.humans.core.util.hideBossBar
import me.ihdeveloper.humans.core.util.region
import me.ihdeveloper.humans.core.util.showBossBar
import me.ihdeveloper.humans.core.util.updateBossBar
import me.ihdeveloper.humans.mine.entity.PrisonMineWizard
import me.ihdeveloper.humans.mine.entity.WIZARD_CRYSTAL_ANIMATION_ROTATING
import me.ihdeveloper.humans.mine.scene.CrystalIntroScene
import me.ihdeveloper.humans.mine.scene.CrystalResetScene
import me.ihdeveloper.humans.mine.scene.ForcedResetScene
import me.ihdeveloper.humans.mine.scene.NormalResetScene
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
    val rewardSpawn: Location,
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
            rewardSpawn = data["rewardSpawn"] as Location,
        )
    }

    val wizard = PrisonMineWizard(wizardSpawn, when (name) {
        "stone-mine" -> PrisonMineWizard.ShopType.STONE
        else -> PrisonMineWizard.ShopType.UNKNOWN
    })

    private val logger = GameLogger("Mine/$name")
    private val bossBar = BossBar("§eReset Time:§f 00:00 §7§l| §eCrystals §7[§c0§7/§a4§7]")

    /** A map of the count of mined blocks by the player */
    private val minersCount = mutableMapOf<String, Int>()

    private val players = mutableSetOf<String>()

    var isResetting = false
    private lateinit var resetScene: Scene
    private var resetTime = RESET_TIME
    private var blocksSize = 0
    private var minedBlocks = 0

    // Award Counter
    var isAwardCounterEnabled = false
    var awardCounter: Long = 0L

    // Crystal Mode
    var crystalMode = false
    var crystalIntroScene: CrystalIntroScene? = null
    private var crystalTitleToggle = false

    init {
        logger.debug("Initializing...")
        blocks.forEach { logger.debug("Loaded block: $it") }
        spawnEntity(wizard, false, logger)

        reset()

        logger.debug("Mine Blocks Size: $blocksSize")
        Bukkit.getScheduler().runTaskTimer(MineSystem.plugin, this, 0L, 1L)
    }

    override fun run() {
        if (crystalIntroScene != null && crystalIntroScene!!.state == SceneState.STOPPED) {
            crystalIntroScene = null
            resetTime = 60L * 20L
        }

        if (isAwardCounterEnabled) {
            if (awardCounter < 0) {
                isAwardCounterEnabled = false
                awardCounter--
                resetScene.resume() // Continue the scene after counting the reward time
                return
            }

            bossBar.title = "§eReward Time: §f${toString(awardCounter)} §7§l| §a§lCOLLECT REWARDS AT THE SQUARE"
            bossBar.current = 100
            bossBar.max = 100

            update {}

            awardCounter--
            return
        }

        if (isResetting) {
            bossBar.title = "§e§lResetting..."
            bossBar.current = 0
            bossBar.max = 100

            update {}
            return
        }

        if (!isResetting && (minedBlocks > 0 || crystalMode) && resetTime < 0) {
            logger.info("Mine($name) invoked the auto reset! ($minedBlocks/$blocksSize, crystal=$crystalMode)")

            isResetting = true
            resetScene = if (crystalMode) {
                CrystalResetScene(this, false)
            } else {
                NormalResetScene(this)
            }
            resetScene.start()
        }

        if (!crystalMode && minedBlocks == 0) {
            resetTime = RESET_TIME
        }

        if (!isResetting && minedBlocks == blocksSize) {
            logger.info("Mine($name) invoked the force reset! (crystal=$crystalMode)")

            isResetting = true
            resetScene = if (crystalMode) {
                CrystalResetScene(this, true)
            } else {
                ForcedResetScene(this)
            }
            resetScene.start()
        }

        if (resetTime % 10 == 0L)
            crystalTitleToggle = !crystalTitleToggle

        bossBar.title = when (crystalMode) {
            true -> "§eReset Time:§f ${toString(resetTime)} §7§l| ${if (crystalTitleToggle) "§e" else "§f"}§lCRYSTAL MODE"
            false -> "§eReset Time:§f ${toString(resetTime)} §7§l| §eCrystals §7[${crystalsSizeToString()}§7/§a4§7]"
        }

        bossBar.current = minedBlocks
        bossBar.max = blocksSize

        update {}

        resetTime--
    }

    override fun serialize(): Map<String, Any> {
        val blocksNames = mutableListOf<String>().apply {
            blocks.forEach { add(it.name) }
        }

        return mapOf(
            "name" to name,
            "regionName" to regionName,
            "pos1" to pos1,
            "pos2" to pos2,
            "blocks" to blocksNames,
            "wizardSpawn" to wizardSpawn,
            "rewardSpawn" to rewardSpawn,
        )
    }

    fun contains(block: Block): Boolean = block.location.betweenBlock(pos1, pos2)

    fun onMine(player: Player, type: Material) {
        // TODO requires rewrite since some items have the same material
        // TODO write a method to get the item type from based on the material
        when (type) {
            Material.STONE -> {
                if (!player.inventory.addGameItem(if (crystalMode) {
                        GameItemStack(PrisonEnchantedStone::class, 1)
                    } else GameItemStack(PrisonStone::class, 1))) {
                    player.sendMessage("§c§lYour inventory is full to collect the mined item!")
                }
            }
            else -> {
                logger.error("Unknown block has been mined (type=$type). (Issued By ${player.name})")
                player.crash("UNKNOWN_MINE_MAT_TYPE")
            }
        }

        minedBlocks++

        val count = minersCount[player.name] ?: 0
        minersCount[player.name] = count + 1
    }

    fun onQuit(player: Player) {
        if (!players.contains(player.name))
            return

        players.remove(player.name)
    }

    fun broadcastCrystal(player: Player) {
        update {
            it.sendMessage("${player.gameName}§e placed the crystal in the wizard table §7[${crystalsSizeToString()}§7/§a4§7]")
        }
    }

    fun broadcastMessage(message: String) {
        update {
            it.sendMessage(message)
        }
    }

    fun reset() {
        resetTime = RESET_TIME
        minedBlocks = 0
        minersCount.clear()

        rebuildBlocks()
        calculateBlocks()
    }

    fun triggerCrystalMode() {
        crystalMode = true
        crystalIntroScene = CrystalIntroScene(this)
        crystalIntroScene!!.start()

        wizard.table.let {
            it.isLocked = true
            it.crystal.animation = WIZARD_CRYSTAL_ANIMATION_ROTATING
        }

        forEach { it.type = Material.BEDROCK }
        minedBlocks = 0
    }

    private fun rebuildBlocks() {
        val random = Random(Random(1000).nextInt())

        val size = blocks.size
        forEach {
            if (it.type === Material.BEDROCK) {
                it.type = blocks[random.nextInt(size)]
            }
        }
    }

    private fun calculateBlocks() {
        blocksSize = 0

        // TODO we can optimize this using a math calculation
        forEach {
            if (it.type !== Material.AIR) {
                for (blockType in blocks) {
                    if (blockType === it.type) {
                        blocksSize++
                        break
                    }
                }
            }
        }
    }

    private fun forEach(block: (block: Block) -> Unit) {
        for (y in pos1.blockY..pos2.blockY) {
            for (x in pos2.blockX..pos1.blockX) {
                for (z in pos1.blockZ..pos2.blockZ) {
                    pos1.world.run {
                       block(getBlockAt(x, y, z))
                    }
                }
            }
        }
    }

    private fun update(block: (player: Player) -> Unit) {
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

            block(player)
        }
    }

    private fun toString(time: Long): String {
        var seconds = time / 20
        var minutes = seconds / 60

        seconds %= 60
        minutes %= 60

        return "${if(minutes <= 9) "0$minutes" else minutes}:${if(seconds <= 9) "0$seconds" else seconds}"
    }

    private fun crystalsSizeToString(): String {
        return when (wizard.table.size) {
            4 -> "§a4"
            3 -> "§63"
            2 -> "§e2"
            1 -> "§f1"
            0 -> "§c0"
            else -> "§7${wizard.table.size}"
        }
    }

    private fun Location.betweenBlock(from: Location, to: Location): Boolean {
        if (from.blockY > blockY || blockY > to.blockY)
            return false
        if (from.blockX < blockX || blockX < to.blockX)
            return false
        if (from.blockZ > blockZ || blockZ > to.blockZ)
            return false
        return true
    }
}