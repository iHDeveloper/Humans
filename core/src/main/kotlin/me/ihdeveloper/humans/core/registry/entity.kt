package me.ihdeveloper.humans.core.registry

import me.ihdeveloper.humans.core.util.GameLogger
import me.ihdeveloper.humans.core.system.CustomEntitySystem
import net.minecraft.server.v1_8_R3.Entity
import net.minecraft.server.v1_8_R3.EntityTypes
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity
import org.bukkit.event.entity.CreatureSpawnEvent
import kotlin.reflect.KClass
import net.minecraft.server.v1_8_R3.EntityLiving
import net.minecraft.server.v1_8_R3.MathHelper
import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory

/**
 * A list of all the summoned entities to keep track of them
 * to remove them when the custom entity system is disposing
 */
val summonedEntities = arrayListOf<Entity>()
val summonedEntitiesInfo = arrayListOf<CustomEntitySystem.EntityInfo>()

private typealias NameToClass = MutableMap<String, Class<out Entity>>  // c
private typealias ClassToName = MutableMap<Class<out Entity>, String>  // d
private typealias IdToClass = MutableMap<Int, Class<out Entity>>       // e
private typealias ClassToId = MutableMap<Class<out Entity>, Int>       // f

/**
 * Overrides an entity in the Minecraft registry.
 */
fun overrideEntity(baseClass: KClass<out Entity>, overrideClass: KClass<out Entity>, logger: GameLogger?) {
    val classToName = getPrivateField<ClassToName>("d")
    val name = classToName[baseClass.java]!!

    val classToId = getPrivateField<ClassToId>("f")
    val id = classToId[baseClass.java]!!

    logger?.debug("Overriding Entity[id=${id}, name=${name}] with ${overrideClass.qualifiedName}...")

    val nameToClass = getPrivateField<NameToClass>("c")
    val idToClass = getPrivateField<IdToClass>("f")

    // Replace base class with override class
    nameToClass[name] = overrideClass.java
    idToClass[id] = overrideClass.java

    // Remove base class references
    classToName.remove(baseClass.java)
    classToId.remove(baseClass.java)

    // Add override class references
    classToName[overrideClass.java] = name
    classToId[overrideClass.java] = id
}

/**
 * Registers an entity to the Minecraft registry
 */
fun registerEntity(customClass: KClass<out Entity>, overrideClass: KClass<out Entity>, logger: GameLogger?) {
    val classToName = getPrivateField<ClassToName>("d")
    val name = classToName[overrideClass.java]!!

    val classToId = getPrivateField<ClassToId>("f")
    val id = classToId[overrideClass.java]!!

    logger?.debug("Registering ${customClass.qualifiedName} as Entity[id=${id}, name=${name}]...")

    // Add custom class references
    classToName[customClass.java] = name
    classToId[customClass.java] = id
}

/**
 * Spawns a custom entity
 */
fun spawnEntity(entity: Entity, logger: GameLogger? = null): Boolean = spawnEntity(entity, true, logger)
fun spawnEntity(entity: Entity, removeWhenFarAway: Boolean, logger: GameLogger? = null): Boolean {
    entity.bukkitEntity.run {
        if (this is CraftLivingEntity)
            this.removeWhenFarAway = removeWhenFarAway
    }
    val result = entity.world.run {
        val chunkX = MathHelper.floor(entity.locX / 16.0)
        val chunkY = MathHelper.floor(entity.locY / 16.0)

        /** Get the craft world and load the chunks from it */
        if (!world.isChunkLoaded(chunkX, chunkY)) {
            logger?.debug("Loading chunk at [chunkX=$chunkX, chunkY=$chunkY]...")
            if (!world.loadChunk(chunkX, chunkY, false)) {
                logger?.error("Failed to load chunk at [chunkX=$chunkX, chunkY=$chunkY]!")
            }
        }

        addEntity(entity, CreatureSpawnEvent.SpawnReason.CUSTOM)
    }
    logger?.debug("Spawning ${entity::class.qualifiedName} [result=$result]")
    return result
}

/**
 * Get a private field in the [EntityTypes]
 */
private inline fun <reified T : Any> getPrivateField(name: String): T {
    val field = EntityTypes::class.java.getDeclaredField(name)
    field.isAccessible = true

    @Suppress("UNCHECKED_CAST")
    return field.get(null) as T
}