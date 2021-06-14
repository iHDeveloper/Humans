package me.ihdeveloper.humans.service.protocol.response

import me.ihdeveloper.humans.service.api.Profile
import me.ihdeveloper.humans.service.protocol.PacketBuffer
import me.ihdeveloper.humans.service.protocol.PacketRegistry
import me.ihdeveloper.humans.service.protocol.PacketResponse
import me.ihdeveloper.humans.service.protocol.PacketResponseStatus

object PacketResponseProfile : PacketResponse(7) {
    init {
        PacketRegistry.register(this::class)
    }

    fun write(source: PacketBuffer, nonce: Int, profile: Profile) {
        super.write(source, nonce, PacketResponseStatus.OK)

        val serializedProfile = PacketRegistry.gson.toJson(profile)
        source.writeInt(serializedProfile.length)
        source.writeUTF(serializedProfile)
    }
}