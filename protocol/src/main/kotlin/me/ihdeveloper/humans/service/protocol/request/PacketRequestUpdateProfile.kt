package me.ihdeveloper.humans.service.protocol.request

import me.ihdeveloper.humans.service.api.Profile
import me.ihdeveloper.humans.service.protocol.PacketBuffer
import me.ihdeveloper.humans.service.protocol.PacketRegistry
import me.ihdeveloper.humans.service.protocol.PacketRequest

object PacketRequestUpdateProfile : PacketRequest(8) {
    init {
        PacketRegistry.register(this::class)
    }

    fun readName(source: PacketBuffer) = source.readUTF(source.readInt())
    fun readProfile(source: PacketBuffer): Profile {
        return PacketRegistry.gson.fromJson(source.readUTF(source.readInt()), Profile::class.java)
    }

    fun write(source: PacketBuffer, nonce: Int, name: String, profile: Profile) {
        super.write(source, nonce)

        source.writeInt(name.length)
        source.writeUTF(name)

        val serializedProfile = PacketRegistry.gson.toJson(profile)
        source.writeInt(serializedProfile.length)
        source.writeUTF(serializedProfile)
    }
}