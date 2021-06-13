package me.ihdeveloper.humans.service.protocol

abstract class PacketRequest(
    type: Int
): Packet(type) {
    open fun write(source: PacketBuffer, nonce: Int) {
        super.writeType(source)
        super.writeNonce(source, nonce)
    }
}
