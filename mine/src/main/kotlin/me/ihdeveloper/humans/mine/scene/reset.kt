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
) : Scene("normal-reset", GameLogger("Scene/Mine-Reset/Normal")) {

    init {
        initFrame {
            mine.wizard.table.isLocked = true
            mine.isResetting = true
        }

        frame(1) {
            mine.broadcastMessage("§7[Wizard] §cOscar: §eAuto reset has been invoked!")
        }

        frame(2 * SECOND) {
            mine.broadcastMessage("§7[Wizard] §cOscar: §cNo rewards to the miners since they didn't finish the mine!")
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
) : Scene("forced-reset", GameLogger("Scene/Mine-Reset/Forced")) {

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
            mine.broadcastMessage("§7[Wizard] §cOscar:§f You have §c60§f seconds to collect them")

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

class CrystalIntroScene(
    mine: Mine
) : Scene("crystal-intro-scene", GameLogger("Scene/Mine-Crystal")) {

    init {
        initFrame {}

        frame(1) {
            mine.broadcastMessage("§7[Wizard] §cOscar:§f Thanks for giving me the prison crystals")
        }

        frame(2 * SECOND) {
            mine.broadcastMessage("§7[Wizard] §cOscar:§f Now is my chance to use my power!")
        }

        frame(4 * SECOND) {
            mine.broadcastMessage("§7[Wizard] §cOscar:§f I will summon magic upon this mine")
        }

        frame(6 * SECOND) {
            mine.reset()
        }

        frame(8 * SECOND) {
            mine.broadcastMessage("§7[Wizard] §cOscar:§a Yes, I made it")
        }

        frame(10 * SECOND) {
            mine.broadcastMessage("§7[Wizard] §cOscar:§f Each block in this mine is in §eEnchanted§f form")
        }

        frame(12 * SECOND) {
            mine.broadcastMessage("§7[Wizard] §cOscar:§f You have §c1§f minute for them to disappear")
        }

        frame(14 * SECOND) {
            mine.broadcastMessage("§7[Wizard] §cOscar:§c Your time starts §f§lNOW§r !")
            stop()
        }

        disposeFrame {
        }
    }

}

class CrystalResetScene(
    mine: Mine,
    forced: Boolean = false,
) : Scene("crystal-reset-scene", GameLogger("Scene/Mine-Reset/Crystal")) {

    private val rewardSquare = RewardSquare(
        mine.rewardSpawn,
        3,
        5,
        3,
        ItemStack(Material.IRON_PICKAXE),
        arrayOf(
            GameItemStack(PrisonCrystal::class, 1),
            GameItemStack(PrisonCrystal::class, 1),
            GameItemStack(PrisonCrystal::class, 1),
            GameItemStack(PrisonCrystal::class, 1),
        )
    )

    private var isRewardSquareSummoned = false

    init {
        initFrame {
            mine.wizard.table.isLocked = true
        }

        everyFrame {
            if (forced && !isRewardSquareSummoned && rewardSquare.isSpawned) {
                isRewardSquareSummoned = true
                resume()
            }
        }

        frame(1) {
            mine.broadcastMessage(when (forced) {
                true -> "§7[Wizard] §cOscar:§a Good job, miners"
                false -> "§7[Wizard] §cOscar:§c Your time is over now!"
            })
        }

        frame(2 * SECOND) {
            if (forced) {
                mine.broadcastMessage("§7[Wizard] §cOscar:§f You will be rewarded for clearing this mine!")
            } else {
                mine.broadcastMessage("§7[Wizard] §cOscar:§f Resetting mine to the original form...")
                mine.crystalMode = false
                mine.reset()
            }
        }

        frame(4 * SECOND) {
            if (forced) {
                mine.broadcastMessage("§7[Wizard] §cOscar:§f Summoning the§a§l SQUARE§r...")
                isRewardSquareSummoned = false
                rewardSquare.spawn()
                pause()
            } else {
                mine.broadcastMessage("§7[Wizard] §cOscar:§c No rewards to the miners since they didn't finish the mine.")
                stop()
            }
        }

        frame(6 * SECOND) {
            if (forced) {
                mine.broadcastMessage("§7[Wizard] §cOscar:§f Miners, you can receive your special rewards now!")
            }
        }

        frame(8 * SECOND) {
            mine.broadcastMessage("§7[Wizard] §cOscar:§f You have §c60§f seconds to collect them")
            mine.awardCounter = 20L * 60L
            mine.isAwardCounterEnabled = true
            pause()
        }

        frame((8 * SECOND) + 1) {
            rewardSquare.destroy()
        }

        frame((8 * SECOND) + 2) {
            mine.broadcastMessage("§7[Wizard] §cOscar:§f The§a§l SQUARE§r has been vanished!")
        }

        frame(9 * SECOND) {
            mine.broadcastMessage("§7[Wizard] §cOscar:§f Resetting the mine...")
        }

        frame((9 * SECOND) + 10) {
            mine.reset()
        }

        frame(10 * SECOND) {
            mine.broadcastMessage("§7[Wizard] §cOscar:§f Get back to work now!")
            stop()
        }

        disposeFrame {
            mine.isResetting = false
            mine.wizard.table.reset(false)
            mine.wizard.table.isLocked = false
            mine.crystalMode = false
        }
    }

}
