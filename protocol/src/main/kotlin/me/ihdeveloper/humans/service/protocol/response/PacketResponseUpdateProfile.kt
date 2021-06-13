package me.ihdeveloper.humans.service.protocol.response

import me.ihdeveloper.humans.service.protocol.PacketRegistry
import me.ihdeveloper.humans.service.protocol.PacketResponse

object PacketResponseUpdateProfile : PacketResponse(7) {
    init {
        PacketRegistry.register(this::class)
    }
}