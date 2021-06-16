package me.ihdeveloper.humans.service.protocol.response

import me.ihdeveloper.humans.service.protocol.PacketBuffer
import me.ihdeveloper.humans.service.protocol.PacketResponse
import me.ihdeveloper.humans.service.protocol.PacketResponseStatus

object PacketResponsePing : PacketResponse(3) {
    fun write(source: PacketBuffer, nonce: Int) {
        super.write(source, nonce, PacketResponseStatus.OK)
    }
}