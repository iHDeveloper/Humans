package me.ihdeveloper.humans.service.protocol.request

import me.ihdeveloper.humans.service.protocol.PacketRegistry
import me.ihdeveloper.humans.service.protocol.PacketRequest

object PacketRequestPing : PacketRequest(0) {
    init {
        PacketRegistry.register(this::class)
    }
}