package me.ihdeveloper.humans.core.scene

import me.ihdeveloper.humans.core.Configuration
import me.ihdeveloper.humans.core.SceneMeta
import me.ihdeveloper.humans.core.entity.PrisonWatcher
import me.ihdeveloper.humans.core.entity.PrisonWitch
import me.ihdeveloper.humans.core.registry.spawnEntity
import me.ihdeveloper.humans.core.system.PlayerSystem
import me.ihdeveloper.humans.core.util.GameLogger
import me.ihdeveloper.humans.core.util.between
import me.ihdeveloper.humans.core.util.freeze
import me.ihdeveloper.humans.core.util.toNMS
import me.ihdeveloper.humans.core.util.toNMSWorld
import me.ihdeveloper.humans.core.util.unfreeze
import net.minecraft.server.v1_8_R3.ItemStack
import net.minecraft.server.v1_8_R3.Items
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

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

        fun save() = config.save()
    }

    init {
        config.run { if (!isLoaded) load() }
    }

    private val spawn = config.get<Location>("spawn")
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

    private var scenario = 0

    init {
        initFrame {
            player.run {
                toNMS().spawnIn(toNMSWorld(location.world))
                teleport(spawn)
                compassTarget = spawn
            }
            pause()
        }

        everyFrame {
            player.run {
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
            pause()
        }

        /** Apply scenario 2 */
        frame(2) {
            player.run {
                addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 1, false, false))
                addPotionEffect(PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 1, false, false))

                sendMessage("§7§oThe door seems to be broken...")
            }

            spawnEntity(watcher, false, logger)
            spawnEntity(witch, false, logger)

            // TODO hide entity from the player

            pause()
        }

        /** Apply scenario 3 */
        frame(3) {
            player.run {
                removePotionEffect(PotionEffectType.BLINDNESS)
                removePotionEffect(PotionEffectType.SLOW)

                sendMessage("§cPrison Watcher: §fI have been watching you...")
            }
        }

        frame(30) {
            player.freeze()
            player.teleport(end)
        }

        frame(60) {
            player.sendMessage("§cPrison Watcher: §fYou shouldn't die this easily.")
        }

        frame(90) {
            player.sendMessage("§cPrison Watcher: §fYou need to suffer living in this prison!")
        }

        frame(120) {
            player.sendMessage("§cPrison Watcher: §fWitch, send this human somewhere unsafe!")
        }

        frame(160) {
            player.sendMessage("§cPrison Witch: §fYessir!")

            witch.equipment[0] = ItemStack(Items.POTION)
            witch.playerName = player.name
        }

        frame(190) {
            player.sendMessage("§cPrison Witch: §fI'm going to send you to...")
        }

        frame (210) {
            player.sendMessage("§cPrison Witch: §eHumans Prison")

            witch.shoot(player.toNMS())
            witch.equipment[0] = null
            pause()
        }

        frame(211) {
            player.run {
                foodLevel = 20
                health = 20.0
                @Suppress("DEPRECATION") sendBlockChange(location, Material.PORTAL, 0)
            }
        }

        frame(301) {
            player.run {
                for (i in 1 until 3)
                    sendMessage("")

                teleport(PlayerSystem.spawn)
                player.unfreeze()
                foodLevel = 20
                health = 20.0
            }
        }

        frame(305) {
            stop()
        }

        disposeFrame {
            watcher.bukkitEntity.remove()
            witch.bukkitEntity.remove()
        }
    }

}