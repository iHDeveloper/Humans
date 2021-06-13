package me.ihdeveloper.humans.service.protocol.response

import me.ihdeveloper.humans.service.protocol.PacketBuffer
import me.ihdeveloper.humans.service.protocol.PacketRegistry
import me.ihdeveloper.humans.service.protocol.PacketResponse
import me.ihdeveloper.humans.service.protocol.PacketResponseStatus

object PacketResponsePing : PacketResponse(1) {
    init {
        PacketRegistry.register(this::class)
    }

    fun write(source: PacketBuffer, nonce: Int) {
        write(source, nonce, PacketResponseStatus.OK)
    }
}