package me.ihdeveloper.humans.service.protocol

import com.google.gson.Gson
import kotlin.reflect.KClass

object PacketRegistry {
    internal val gson = Gson()

    private val instances = mutableMapOf<Short, Packet>()

    fun get(source: PacketBuffer): Packet? {
        val type = source.readShort()
        return instances[type]
    }

    internal fun register(packetLoader: KClass<out Packet>) {
        val obj = packetLoader.objectInstance ?: error("Failed to register a packet without an object instance! (${packetLoader.qualifiedName})")
        instances[obj.packetType.toShort()] = obj
    }
}