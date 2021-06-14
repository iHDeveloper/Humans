package me.ihdeveloper.humans.service.protocol.request

import me.ihdeveloper.humans.service.protocol.PacketBuffer
import me.ihdeveloper.humans.service.protocol.PacketRegistry
import me.ihdeveloper.humans.service.protocol.PacketRequest

object PacketRequestHello : PacketRequest(0) {
    init {
        PacketRegistry.register(this::class)
    }

    fun readTimeout(source: PacketBuffer) = source.readInt()

    fun write(source: PacketBuffer, nonce: Int, timeout: Int) {
        super.write(source, nonce)
        source.writeInt(timeout)
    }
}