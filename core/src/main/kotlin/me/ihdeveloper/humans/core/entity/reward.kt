package me.ihdeveloper.humans.core.entity

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import me.ihdeveloper.humans.core.GameItemStack
import me.ihdeveloper.humans.core.corePlugin
import me.ihdeveloper.humans.core.registry.createItem
import me.ihdeveloper.humans.core.registry.spawnEntity
import me.ihdeveloper.humans.core.setPrivateField
import me.ihdeveloper.humans.core.util.addGameItem
import me.ihdeveloper.humans.core.util.findEntities
import net.minecraft.server.v1_8_R3.Entity
import net.minecraft.server.v1_8_R3.EntityPlayer
import net.minecraft.server.v1_8_R3.TileEntityBeacon
import org.bukkit.Bukkit
import org.bukkit.Effect
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitTask
import org.bukkit.util.Vector

private const val ITEM_YAW_SPEED = 2F

private val deltas = arrayOf(
    Vector(1, 0, 0),
    Vector(0, 0, 1),
    Vector(-1, 0, 0),
    Vector(0, 0, -1),

    Vector(1, 0, 1),        // Top Right
    Vector(-1, 0, -1),      // Bottom Left
    Vector(1, 0, -1),       // Bottom Right
    Vector(-1, 0, 1),       // Top Left
)

/**
 * A square that contains rewards for the player
 */
class RewardSquare(
    private val location: Location,
    private val color: Byte,
    private val borderRadius: Int,
    private val rewardRadius: Int,
    hologram: ItemStack,
    private val items: Array<GameItemStack>,
) : Runnable {
    var isSpawned = false

    private val blocksStates = mutableMapOf<Location, Material>()
    private lateinit var bukkitTask: BukkitTask
    private val itemEntities = arrayListOf<RewardItem>()
    private val itemHologram = ItemHologram(location.clone().apply {
        add(3.0, 7.0, -2.0)
    }, hologram)

    private var spawnedHologram = false
    private var currentBorderRadius = 1
    private var currentSpawnedItems = 0
    private var threshold = false
    private var ticks = 0

    fun spawn() {
        bukkitTask = Bukkit.getScheduler().runTaskTimer(corePlugin, this, 0L, 1L)
        location.block.let { block ->
            block.update(Material.STAINED_GLASS)

            @Suppress("DEPRECATION")
            block.data = color
        }

        spawnEntity(RewardLightning(location.clone().apply { y += 1.0 }), false, null)
    }

    fun destroy() {
        isSpawned = false
        bukkitTask.cancel()

        itemHologram.die()

        /** Reset the current blocks states back to their original one */
        blocksStates.forEach {
            it.key.block.let { block ->
                block.setType(it.value, false)

                @Suppress("DEPRECATION")
                block.data = 0
            }

        }
        blocksStates.clear()

        /** Kill all item entities */
        itemEntities.forEach { it.die() }
        itemEntities.clear()
    }

    override fun run() {
        ticks++

        if (ticks == 4 && !spawnedHologram) {
            spawnedHologram = true
            spawnEntity(itemHologram, false, null)
        }

        if (ticks % 5 == 0) {
            if (currentBorderRadius <= borderRadius) {
                deltas.forEach {
                    val delta = it.clone()
                    val pos1 = location.clone()
                    pos1.add(delta)

                    val pos2 = location.clone()
                    delta.multiply(currentBorderRadius)
                    pos2.add(delta)

                    when (it) {
                        deltas[4] -> fill(pos1, pos2, 1, 1)
                        deltas[5] -> fill(pos1, pos2, -1, -1)
                        deltas[6] -> fill(pos1, pos2, 1, -1)
                        deltas[7] -> fill(pos1, pos2, -1, 1)
                        else -> location.world.getBlockAt(pos2.blockX, pos2.blockY, pos2.blockZ).let { block ->
                            block.update(Material.STAINED_CLAY)

                            @Suppress("DEPRECATION")
                            block.data = color
                        }
                    }
                }

                currentBorderRadius++
            } else {
                if (currentSpawnedItems >= items.size) {
                    if (!threshold) {
                        if (ticks % 5L == 0L) {
                            itemEntities.forEach { it.collectable = true }

                            spawnEntity(RewardLightning(location.clone().apply { y += 1.0 }), false, null)
                            location.clone().run {
                                subtract(0.0, 1.0, 0.0)
                                block.update(Material.BEACON)

                                subtract(1.0, 1.0, 1.0)

                                fill(this, clone().add(3.0, 0.0, 3.0), 1, 1, Material.IRON_BLOCK, 0)
                            }
                            threshold = true
                        }
                    } else {
                        isSpawned = true
                        bukkitTask.cancel()
                    }
                    return
                }

                if (currentSpawnedItems < items.size) {

                    val anglePerItem = 360 / (currentSpawnedItems + 1)

                    var angle = 0

                    val itemEntity = RewardItem(location.clone().apply {
                        add(0.0, 1.0, 0.0)

                        val radian = (angle * PI) / 180

                        x = location.x + (rewardRadius * cos(radian))
                        z = location.z + (rewardRadius * sin(radian))

                        clone().run {
                            add(0.0, -0.75, 0.0)
                            world.spigot().playEffect(this, Effect.SMOKE)
                        }
                    }, items[currentSpawnedItems])

                    itemEntities.forEach {
                        angle += anglePerItem

                        val radian = (angle * PI) / 180
                        it.rewardLocation.x = location.x + (rewardRadius * cos(radian))
                        it.rewardLocation.z = location.z + (rewardRadius * sin(radian))

                        it.setLocation()
                    }

                    spawnEntity(itemEntity, false, null)
                    itemEntities.add(itemEntity)

                    currentSpawnedItems++
                }
            }
        }
    }

    private fun Block.update(type: Material) {
        if (blocksStates.containsKey(location))
            return

        blocksStates[location] = this.type

        setType(type, false)
    }

    private fun fill(
        pos1: Location,
        pos2: Location,
        xStep: Int,
        zStep: Int,
        type: Material = Material.STAINED_CLAY,
        data: Byte = color
    ) {
        var x = pos1.blockX

        while (x != pos2.blockX) {
            var z = pos1.blockZ
            while (z != pos2.blockZ) {
                pos1.world.getBlockAt(x, pos1.blockY, z).let { block ->
                    block.update(type)

                    @Suppress("DEPRECATION")
                    block.data = data
                }

                z += zStep
            }

            x += xStep
        }

        pos1.world.getBlockAt(pos2.blockX, pos1.blockY, pos2.blockZ).let { block ->
            block.update(Material.STAINED_CLAY)

            @Suppress("DEPRECATION")
            block.data = color
        }
    }
}

/**
 * A lighting with no effect on the world
 */
class RewardLightning(
    location: Location
) : CustomLightning(location, true)

/**
 * An entity of an item rotating as reward for the player to collect
 */
class RewardItem(
    location: Location,
    private val item: GameItemStack,
): CustomArmorStand(location) {
    // TODO Make each item special for selected player to take it (meaning packets work)

    var collectable = false

    internal val rewardLocation: Location
        get() = location

    init {
        customName = "§7x${item.amount} $item"
        customNameVisible = true
        isInvisible = true
        setGravity(false)
        setLocation()

        setEquipment(0, createItem(item.type, item.amount))
    }

    private var collected = false

    override fun t_() {
        super.t_()

        if (collected)
            return

        if (collectable) {
            for (entity in world.world.getNearbyEntities(location, 0.5, 0.5, 0.5)) {
                if (entity.type !== EntityType.PLAYER)
                    continue

                (entity as Player).let { player ->
                    player.inventory.addGameItem(item)
                    player.sendMessage("§eYou got §7x${item.amount} $item")
                }
                customNameVisible = false
                setEquipment( 0, null)
                collected = true
                return
            }
        }

        location.yaw += ITEM_YAW_SPEED
        setLocation()
    }

}
