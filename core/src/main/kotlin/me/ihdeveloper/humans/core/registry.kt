package me.ihdeveloper.humans.core

import net.minecraft.server.v1_8_R3.Entity
import net.minecraft.server.v1_8_R3.EntityTypes
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity
import org.bukkit.event.entity.CreatureSpawnEvent
import kotlin.reflect.KClass

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
fun spawnEntity(entity: Entity): org.bukkit.entity.Entity = spawnEntity(entity, true)
fun spawnEntity(entity: Entity, removeWhenFarAway: Boolean): org.bukkit.entity.Entity {
    entity.world.addEntity(entity, CreatureSpawnEvent.SpawnReason.CUSTOM)
    if (removeWhenFarAway)
        (entity as CraftLivingEntity).removeWhenFarAway = false
    return entity.bukkitEntity
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