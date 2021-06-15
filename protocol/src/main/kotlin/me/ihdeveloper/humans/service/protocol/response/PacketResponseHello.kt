package me.ihdeveloper.humans.service.protocol.response

import me.ihdeveloper.humans.service.protocol.PacketBuffer
import me.ihdeveloper.humans.service.protocol.PacketRegistry
import me.ihdeveloper.humans.service.protocol.PacketResponse
import me.ihdeveloper.humans.service.protocol.PacketResponseStatus

object PacketResponseHello : PacketResponse(1) {
    fun readName(source: PacketBuffer) = source.readUTF(source.readInt())

    fun write(source: PacketBuffer, nonce: Int, name: String) {
        super.write(source, nonce, PacketResponseStatus.OK)
        source.writeInt(name.length)
        source.writeUTF(name)
    }
}