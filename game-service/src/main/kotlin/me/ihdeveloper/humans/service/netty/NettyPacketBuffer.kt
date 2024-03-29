package me.ihdeveloper.humans.service.netty

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import java.nio.charset.Charset
import me.ihdeveloper.humans.service.protocol.PacketBuffer

class NettyPacketBuffer(
    internal val buf: ByteBuf
) : PacketBuffer {
    companion object {
        fun alloc() = NettyPacketBuffer(Unpooled.buffer(8))
        fun wrapped(buf: ByteBuf) = NettyPacketBuffer(buf)
    }

    override fun readShort() = buf.readShort()

    override fun readInt() = buf.readInt()

    override fun readUTF(length: Int): String {
        buf.skipBytes(length)
        return buf.toString(buf.readerIndex() - length, length, Charset.forName("UTF-8"))
    }

    override fun skipBytes(length: Int) {
        buf.skipBytes(length)
    }

    override fun writeShort(value: Int) {
        buf.writeShort(value)
    }

    override fun writeInt(value: Int) {
        buf.writeInt(value)
    }

    override fun writeUTF(value: String) {
        buf.writeBytes(value.toByteArray(Charsets.UTF_8))
    }

    override fun retain() {
        buf.retain()
    }

    override fun release() {
        buf.release()
    }
}