package me.ihdeveloper.humans.service.protocol.response

import me.ihdeveloper.humans.service.api.GameTime
import me.ihdeveloper.humans.service.protocol.PacketBuffer
import me.ihdeveloper.humans.service.protocol.PacketRegistry
import me.ihdeveloper.humans.service.protocol.PacketResponse
import me.ihdeveloper.humans.service.protocol.PacketResponseStatus

object PacketResponseTime : PacketResponse(3) {
    init {
        PacketRegistry.register(this::class)
    }

    fun write(source: PacketBuffer, nonce: Int, time: GameTime) {
        super.write(source, nonce, PacketResponseStatus.OK)

        val serializedTime = PacketRegistry.gson.toJson(time)
        source.writeInt(serializedTime.length)
        source.writeUTF(serializedTime)
    }
}