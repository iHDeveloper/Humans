package me.ihdeveloper.humans.service.protocol.request

import me.ihdeveloper.humans.service.protocol.PacketRegistry
import me.ihdeveloper.humans.service.protocol.PacketRequest

object PacketRequestTime : PacketRequest(4) {
    init {
        PacketRegistry.register(this::class)
    }
}