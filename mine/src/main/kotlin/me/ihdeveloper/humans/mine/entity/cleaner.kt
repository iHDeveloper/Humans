package me.ihdeveloper.humans.mine.entity

import java.util.LinkedList
import java.util.Queue
import kotlin.random.Random
import me.ihdeveloper.humans.core.entity.CustomFallingBlock
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.util.Vector

private const val CLEANER_FALL_SPEED = 0.5F

private val deltas = arrayOf(
    Vector(1, 0, 0),
    Vector(-1, 0, 0),
    Vector(0, 0, 1),
    Vector(0, 0, -1),
    Vector(0, 1, 0),
)

class PrisonMineCleaner(
    type: Material,
    location: Location,
    private val set: Set<Material>,
    private val cleaned: MutableSet<BlockData> = mutableSetOf(),
) : CustomFallingBlock(type, location) {
    data class BlockData(
        val x: Int,
        val y: Int,
        val z: Int,
        val layer: Int,
    )

    companion object {
        private val random = Random(Random(1000).nextInt())
    }

    private val x = location.x
    private val z = location.z
    private val queue: Queue<BlockData> = LinkedList()
    private val list = set.toList()

    private val isCleaning: Boolean
        get() = cleaning

    private var y = location.y
    private var cleaning = false
    private var currentLayer = 0
    private var ticks = 0

    init {
        dropItem = false
        hurtEntities = false
    }

    fun onCleaning() {
        ticks++

        if (ticks % 5 != 0) {
            return
        }

        while (!queue.isEmpty()) {
            val blockData = queue.peek()

            if (blockData.layer != currentLayer) {
                currentLayer++
                return
            }

            queue.poll()

            if (cleaned.contains(blockData)) {
                continue
            }

            with (blockData) {
                val block = world.world.getBlockAt(x, y, z)

                block.type = randomType()

                for (delta in deltas) {
                    val newX = x + delta.blockX
                    val newY = y + delta.blockY
                    val newZ = z + delta.blockZ

                    val newBlock = world.world.getBlockAt(newX, newY, newZ)
                    newBlock.type = Material.BEDROCK

                    if (set.contains(newBlock.type) || newBlock.type === Material.BEDROCK) {
                        queue.add(BlockData(newX, newY, newZ, currentLayer + 1))
                    }
                }
            }
        }

        cleaning = false
    }

    override fun t_() {
        if (cleaning) {
            onCleaning()
            return
        }

        val block = world.world.getBlockAt(x.toInt(), y.toInt(), z.toInt())

        if (block.type === Material.AIR) {
            y -= CLEANER_FALL_SPEED
            setPosition(x, y, z)
            return
        }

        if (block.type !== Material.BEDROCK && !set.contains(block.type)) {
            die()
            return
        } else {
            queue.add(BlockData(x.toInt(), y.toInt(), z.toInt(), currentLayer))

            block.type = Material.BEDROCK
            cleaning = true
            return
        }
    }

    private fun randomType() = list[random.nextInt(list.size)]
}
