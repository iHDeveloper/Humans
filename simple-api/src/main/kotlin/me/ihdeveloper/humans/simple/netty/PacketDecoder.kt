package me.ihdeveloper.humans.simple.netty

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder

internal class PacketDecoder : ByteToMessageDecoder() {
    private var writing = false
    private var targetLength: Int = 0
    private lateinit var buffer: NettyPacketBuffer

    override fun decode(ctx: ChannelHandlerContext, source: ByteBuf, out: MutableList<Any>) {
        while (source.isReadable) {
            val currentLength = source.readableBytes()
            if (writing) {
                if (currentLength >= targetLength) {
                    writing = false
                    buffer.buf.writeBytes(source, targetLength)
                    targetLength = 0
                    val duplicate = buffer.buf.duplicate()
                    duplicate.retain()
                    out.add(NettyPacketBuffer.wrapped(duplicate))
                    buffer.release()
                    continue
                }

                writing = true
                targetLength -= currentLength
                buffer.buf.writeBytes(source, currentLength)
            } else {
                buffer = NettyPacketBuffer.alloc()
                targetLength = source.readInt()
                if (currentLength >= targetLength) {
                    writing = false
                    buffer.buf.writeBytes(source, targetLength)
                    targetLength = 0
                    val duplicate = buffer.buf.duplicate()
                    duplicate.retain()
                    out.add(NettyPacketBuffer.wrapped(duplicate))
                    buffer.release()
                    continue
                }

                writing = true
                targetLength -= currentLength
                buffer.buf.writeBytes(source, currentLength)
            }
        }
    }
}