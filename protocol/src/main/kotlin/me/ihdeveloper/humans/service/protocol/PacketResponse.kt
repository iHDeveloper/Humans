package me.ihdeveloper.humans.service.protocol

abstract class PacketResponse(
    type: Int
) : Packet(type) {
    open fun write(source: PacketBuffer, nonce: Int, status: PacketResponseStatus) {
        super.writeType(source)
        super.writeNonce(source, nonce)
        writeStatus(source, status)
    }

    private fun writeStatus(source: PacketBuffer, status: PacketResponseStatus) {
        source.writeShort(status.code)
    }
}