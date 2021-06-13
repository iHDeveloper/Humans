package me.ihdeveloper.humans.service.protocol.request

import me.ihdeveloper.humans.service.api.Profile
import me.ihdeveloper.humans.service.protocol.PacketBuffer
import me.ihdeveloper.humans.service.protocol.PacketRegistry
import me.ihdeveloper.humans.service.protocol.PacketRequest

object PacketRequestUpdateProfile : PacketRequest(6) {
    init {
        PacketRegistry.register(this::class)
    }

    fun write(source: PacketBuffer, nonce: Int, profile: Profile) {
        super.write(source, nonce)

        val serializedProfile = PacketRegistry.gson.toJson(profile)
        source.writeInt(serializedProfile.length)
        source.writeUTF(serializedProfile)
    }
}