package me.ihdeveloper.humans.service.protocol.request

import me.ihdeveloper.humans.service.protocol.PacketBuffer
import me.ihdeveloper.humans.service.protocol.PacketRegistry
import me.ihdeveloper.humans.service.protocol.PacketRequest

object PacketRequestProfile : PacketRequest(6) {
    fun readName(source: PacketBuffer) = source.readUTF(source.readInt())

    fun write(source: PacketBuffer, nonce: Int, name: String) {
        super.write(source, nonce)

        source.writeInt(name.length)
        source.writeUTF(name)
    }
}
