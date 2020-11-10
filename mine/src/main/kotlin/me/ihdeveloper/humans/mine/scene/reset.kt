package me.ihdeveloper.humans.mine.scene

import me.ihdeveloper.humans.core.GameItemStack
import me.ihdeveloper.humans.core.Scene
import me.ihdeveloper.humans.core.entity.RewardSquare
import me.ihdeveloper.humans.core.item.PrisonCrystal
import me.ihdeveloper.humans.core.item.PrisonEnchantedStone
import me.ihdeveloper.humans.core.item.PrisonStone
import me.ihdeveloper.humans.core.util.GameLogger
import me.ihdeveloper.humans.mine.Mine
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

private const val SECOND = 20L

/**
 * When the mine reset is being invoked by the auto-reset
 *
 * - No Rewards
 * - No animation during the reset
 */
class NormalResetScene(
    mine: Mine
) : Scene("normal-reset", GameLogger("Scene/Reset/Normal")) {

    init {
        initFrame {
            mine.wizard.table.isLocked = true
            mine.isResetting = true
        }

        frame(1) {
            mine.broadcastMessage("§7[Wizard] §cOscar: §eAuto reset has been invoked!")
        }

        frame(2 * SECOND) {
            mine.broadcastMessage("§7[Wizard] §cOscar: §cNo awards to the miners since they didn't finish the mine!")
            stop()
        }

        disposeFrame {
            mine.reset()
            mine.wizard.table.isLocked = false
            mine.isResetting = false
        }
    }

}

/**
 * When the mine reset is being invoked because it's empty!
 *
 * - Rewards (Enchanted Stone 1-20, Prison Crystal 1-2)
 * - No animation during the reset
 */
class ForcedResetScene(
    mine: Mine
) : Scene("forced-reset", GameLogger("Scene/Reset/Forced")) {

    private val rewardSquare = RewardSquare(
        mine.rewardSpawn,
        7,
        5,
        3,
        ItemStack(Material.STONE_PICKAXE),
        arrayOf(
            GameItemStack(PrisonCrystal::class, 1),
            GameItemStack(PrisonCrystal::class, 1),
            GameItemStack(PrisonEnchantedStone::class, 5),
            GameItemStack(PrisonEnchantedStone::class, 3),
            GameItemStack(PrisonEnchantedStone::class, 4),
            GameItemStack(PrisonEnchantedStone::class, 5),
            GameItemStack(PrisonStone::class, 64),
        )
    )

    private var isRewardSpawned = false

    init {
        initFrame {
            mine.wizard.table.isLocked = true
            mine.isResetting = true
        }

        everyFrame {
            if (!isRewardSpawned && rewardSquare.isSpawned) {
                isRewardSpawned = true
                resume()
            }
        }

        frame(1) {
            mine.broadcastMessage("§7[Wizard] §cOscar:§a Good job, miners!")
        }

        frame(2 * SECOND) {
            mine.broadcastMessage("§7[Wizard] §cOscar:§f Resetting the mine...")
        }

        frame((4 * SECOND) + 10) {
            mine.reset()
        }

        frame(6 * SECOND) {
            mine.broadcastMessage("§7[Wizard] §cOscar:§f Miners, you deserve some rewards for your work!")
        }

        frame(8 * SECOND) {
            mine.broadcastMessage("§7[Wizard] §cOscar:§f Summoning the§a§l SQUARE§r...")
            rewardSquare.spawn()
            pause() // so when the square is summoned, the scene resumes
        }

        frame((8 * SECOND) + 1) {
            mine.broadcastMessage("§7[Wizard] §cOscar:§f Miners, you can receive your rewards now!")
        }

        frame(10 * SECOND) {
            mine.broadcastMessage("§7[Wizard] §cOscar:§f You have §c60§f seconds to collect the rewards")

            mine.isAwardCounterEnabled = true
            mine.awardCounter = 20L * 60L
            pause()
        }

        frame ((10 * SECOND) + 1) {
            rewardSquare.destroy()
        }

        frame((10 * SECOND) + 2) {
            mine.broadcastMessage("§7[Wizard] §cOscar:§f The§a§l SQUARE§r has been vanished!")
        }

        frame (12 * SECOND) {
            mine.broadcastMessage("§7[Wizard] §cOscar:§f Get back to work now!")
            stop()
        }

        disposeFrame {
            mine.wizard.table.isLocked = false
            mine.isResetting = false
        }
    }

}
