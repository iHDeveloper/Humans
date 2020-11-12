package me.ihdeveloper.humans.core.scene

import me.ihdeveloper.humans.core.Configuration
import me.ihdeveloper.humans.core.GameItemStack
import me.ihdeveloper.humans.core.SceneMeta
import me.ihdeveloper.humans.core.entity.PrisonWatcher
import me.ihdeveloper.humans.core.entity.PrisonWitch
import me.ihdeveloper.humans.core.item.PrisonCursedPickaxe
import me.ihdeveloper.humans.core.system.GAME_MENU
import me.ihdeveloper.humans.core.system.PlayerSystem
import me.ihdeveloper.humans.core.system.ProfileSystem
import me.ihdeveloper.humans.core.util.GameLogger
import me.ihdeveloper.humans.core.util.addGameItem
import me.ihdeveloper.humans.core.util.between
import me.ihdeveloper.humans.core.util.freeze
import me.ihdeveloper.humans.core.util.toNMS
import me.ihdeveloper.humans.core.util.unfreeze
import net.minecraft.server.v1_8_R3.ItemStack
import net.minecraft.server.v1_8_R3.Items
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.util.Vector

private const val SECOND = 20L

/**
 * Making the new player trying to escape a mysterious place in the game
 * called "Humans slaughter".
 *
 * Scenario 1:
 * The player spawns in a cell with broken door. He's feeling uncomfortable and confused.
 *
 * Scenario 2:
 * When the player leaves the door, He gets blind. And, he is saying "The door seems to be broken"
 *
 * Scenario 3:
 * The player enters a dark hall
 *
 */
class IntroScene(
    player: Player
): IndividualScene(player, "Intro", GameLogger("Scene/Intro")) {
    companion object: SceneMeta {
        override val config = Configuration("scene-intro")

        override fun save() = config.save()
    }

    init {
        config.run { if (!isLoaded) load() }
    }

    val spawn = config.get<Location>("spawn")
    private val watcherSpawn = config.get<Location?>("watcher")
    private val witchSpawn = config.get<Location?>("witch")
    private val end = config.get<Location>("end")

    /** Scenario 1 */
    private val pos1 = config.get<Location>("pos1")
    private val pos2 = config.get<Location>("pos2")

    /** Scenario 2 */
    private val pos3 = config.get<Location>("pos3")
    private val pos4 = config.get<Location>("pos4")

    /** Scenario 3 */
    private val pos5 = config.get<Location>("pos5")
    private val pos6 = config.get<Location>("pos6")

    init {
        if (watcherSpawn == null) {
            logger.error("Watcher spawn not found in the config")
        } else if (witchSpawn == null) {
            logger.error("Witch spawn not found in the config")
        }
    }

    private val watcher = PrisonWatcher(watcherSpawn!!)
    private val witch = PrisonWitch(witchSpawn!!)
    private var thrownPotion: PrisonWitch.Potion? = null

    private var scenario = 0

    init {
        initFrame {
            player.run {
                inventory.clear()
                removePotionEffect(PotionEffectType.BLINDNESS)
                removePotionEffect(PotionEffectType.SLOW)
            }
            pause()
        }

        everyFrame {
            player.run {
                toNMS().also {
                    if (watcher.isAnimating) {
                        watcher.updateMove(it)
                    }

                    val oldLoc = thrownPotion?.run { Vector(locX, locY, locZ) }
                    thrownPotion?.onTick()
                    val delta = thrownPotion?.run { Vector(locX, locY, locZ) }
                    delta?.subtract(oldLoc)
                    delta?.run { thrownPotion?.updatePos(it, x, y, z) }
                }

                world.players.forEach {
                    if (canSee(it) && toNMS() !== it.toNMS() )
                        hidePlayer(it)
                }

                if (scenario == 0 && location.between(pos1, pos2)) {
                    scenario++
                    resume()
                } else if (scenario == 1 && location.between(pos3, pos4)) {
                    scenario++
                    resume()
                } else if (scenario == 2 && location.between(pos5, pos6)) {
                    scenario++
                    resume()
                }
            }
        }

        /** Apply scenario 1 */
        frame(1) {
            player.run {
                foodLevel = 0
                health = 0.5
            }
        }

        frame(SECOND) {
            player.sendMessage("§7§oYou are in an unknown location")
        }

        frame(SECOND + 10) {
            player.sendMessage("§7§oYou don't remember what happened to you.")
        }

        frame(SECOND * 2) {
            player.sendMessage("§7§oYou seem to be in a suspicious place.")
            pause()
        }

        /** Apply scenario 2 */
        frame((SECOND * 2) + 1) {
            player.run {
                addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 1, false, false))
                addPotionEffect(PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 1, false, false))

                sendMessage("§7§oThe door seems to be broken...")

                toNMS().also {
                    watcher.spawnToPlayer(it)
                    watcher.startAnimation()

                    witch.spawnToPlayer(it)
                }
            }

            // TODO hide entity from the player

            pause()
        }

        /** Apply scenario 3 */
        frame((SECOND * 2) + 2) {
            player.run {
                removePotionEffect(PotionEffectType.BLINDNESS)
                removePotionEffect(PotionEffectType.SLOW)

                sendMessage("§cPrison Watcher: §fI have been watching you, Human.")
            }
        }

        frame((SECOND * 4) + 2) {
            player.teleport(end)
            player.freeze()
        }

        frame((SECOND * 6) + 2) {
            player.sendMessage("§cPrison Watcher: §fYou shouldn't die peacefully.")
        }

        frame((SECOND * 8) + 2) {
            player.sendMessage("§cPrison Watcher: §fYou shall suffer living in this misery!")
        }

        frame((SECOND * 10) + 2) {
            player.sendMessage("§cPrison Watcher: §fWitch, give this \"Human\" a cursed pickaxe!")
        }

        frame((SECOND * 12) + 2) {
            player.sendMessage("§cPrison Witch: §fYessir!")
            witch.equipment[0] = ItemStack(Items.WOODEN_PICKAXE)
            witch.updateInventory(player.toNMS())
        }

        frame((SECOND * 14) + 2) {
            player.inventory.addGameItem(GameItemStack(PrisonCursedPickaxe::class))
            witch.equipment[0] = null
            witch.updateInventory(player.toNMS())
        }

        frame((SECOND * 16) + 2) {
            player.sendMessage("§cPrison Watcher: §fWitch, send this \"Human\" somewhere unsafe!")
        }

        frame((SECOND * 18) + 2) {
            player.sendMessage("§cPrison Witch: §fYessir!")

            witch.equipment[0] = ItemStack(Items.POTION)
            witch.playerName = player.name
            witch.updateInventory(player.toNMS())
        }

        frame((SECOND * 20) + 2) {
            player.sendMessage("§cPrison Witch: §fI'm going to send this \"Human\" to...")
        }

        frame ((SECOND * 22) + 2) {
            player.sendMessage("§cPrison Witch: §e§lHumans Prison")

            player.toNMS().also {
                thrownPotion = witch.shoot(it)
                witch.equipment[0] = null
                witch.updateInventory(it)
                thrownPotion!!.spawnToPlayer(it)
            }

            pause()
        }

        frame((SECOND * 22) + 3) {
            player.run {
                foodLevel = 20
                health = 20.0
                @Suppress("DEPRECATION") sendBlockChange(location, Material.PORTAL, 0)
            }
        }

        frame((SECOND * 26) + 3) {
            player.run {
                for (i in 1 until 3)
                    sendMessage("")

                player.unfreeze()
                teleport(PlayerSystem.spawn)
                inventory.setItem(8, GAME_MENU)
                foodLevel = 20
                health = 20.0
            }

            ProfileSystem.profiles[player.name]?.new = false
        }

        frame((SECOND * 26) + 5) {
            Bukkit.getOnlinePlayers().forEach {
                if (!player.canSee(it) && player.toNMS() !== it.toNMS())
                    player.showPlayer(it)
            }

            stop()
        }

        disposeFrame {
            player.toNMS().run {
                watcher.destroy(this)
                witch.destroy(this)
            }
        }
    }

}