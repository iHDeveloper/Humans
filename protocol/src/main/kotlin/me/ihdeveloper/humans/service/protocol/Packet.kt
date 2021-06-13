package me.ihdeveloper.humans.service.protocol

abstract class Packet(
    internal val packetType: Int
) {
    fun readType(source: PacketBuffer): Short {
        return source.readShort()
    }

    fun readNonce(source: PacketBuffer): Short {
        return source.readShort()
    }

    protected fun writeType(source: PacketBuffer) {
        source.writeShort(packetType)
    }

    protected fun writeNonce(source: PacketBuffer, nonce: Int) {
        source.writeInt(nonce)
    }
}