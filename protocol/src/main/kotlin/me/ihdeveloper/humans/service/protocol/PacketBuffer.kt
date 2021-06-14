package me.ihdeveloper.humans.service.protocol

/**
 * Provides the ability to implement different byte buffers with different dependency versions
 */
interface PacketBuffer {
    fun readShort(): Short
    fun readInt(): Int
    fun readUTF(length: Int): String

    fun skipBytes(length: Int)

    fun writeShort(value: Int)
    fun writeInt(value: Int)
    fun writeUTF(value: String)

    fun release()
}
