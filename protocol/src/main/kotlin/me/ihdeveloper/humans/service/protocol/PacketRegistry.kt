package me.ihdeveloper.humans.service.protocol

import com.google.gson.Gson
import kotlin.reflect.KClass
import me.ihdeveloper.humans.service.protocol.request.PacketRequestHello
import me.ihdeveloper.humans.service.protocol.request.PacketRequestPing
import me.ihdeveloper.humans.service.protocol.request.PacketRequestProfile
import me.ihdeveloper.humans.service.protocol.request.PacketRequestTime
import me.ihdeveloper.humans.service.protocol.request.PacketRequestUpdateProfile
import me.ihdeveloper.humans.service.protocol.response.PacketResponseHello
import me.ihdeveloper.humans.service.protocol.response.PacketResponsePing
import me.ihdeveloper.humans.service.protocol.response.PacketResponseProfile
import me.ihdeveloper.humans.service.protocol.response.PacketResponseTime
import me.ihdeveloper.humans.service.protocol.response.PacketResponseUpdateProfile

object PacketRegistry {
    internal val gson = Gson()
    private val instances = mutableMapOf<Short, Packet>()

    fun init() {
        register(PacketRequestHello::class)
        register(PacketResponseHello::class)

        register(PacketRequestPing::class)
        register(PacketResponsePing::class)

        register(PacketRequestTime::class)
        register(PacketResponseTime::class)

        register(PacketRequestProfile::class)
        register(PacketResponseProfile::class)

        register(PacketRequestUpdateProfile::class)
        register(PacketResponseUpdateProfile::class)
    }

    fun get(source: PacketBuffer): Packet? {
        val type = source.readShort()
        return get(type)
    }

    fun get(type: Short): Packet? {
        return instances[type]
    }

    private fun register(packetLoader: KClass<out Packet>) {
        val obj = packetLoader.objectInstance ?: error("Failed to register a packet without an object instance! (${packetLoader.qualifiedName})")
        instances[obj.packetType.toShort()] = obj
    }
}