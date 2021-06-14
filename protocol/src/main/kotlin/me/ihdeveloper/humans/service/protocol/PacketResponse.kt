package me.ihdeveloper.humans.service.protocol

abstract class PacketResponse(
    type: Int
) : Packet(type) {
    open fun write(source: PacketBuffer, nonce: Int, status: PacketResponseStatus) {
        super.writeType(source)
        super.writeNonce(source, nonce)
        writeStatus(source, status)
    }

    fun skipStatus(source: PacketBuffer) = source.skipBytes(2)

    fun readStatus(source: PacketBuffer): PacketResponseStatus {
        val code = source.readShort()
        PacketResponseStatus.values().forEach { value ->
            if (value.code == code.toInt()) {
                return value
            }
        }
        return PacketResponseStatus.INTERNAL_ERROR
    }

    private fun writeStatus(source: PacketBuffer, status: PacketResponseStatus) {
        source.writeShort(status.code)
    }
}