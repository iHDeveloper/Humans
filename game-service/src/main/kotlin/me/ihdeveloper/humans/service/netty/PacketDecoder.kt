package me.ihdeveloper.humans.service.netty

import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufUtil
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder

class PacketDecoder : ByteToMessageDecoder() {
    private var writing = false
    private var targetLength: Int = 0
    private lateinit var buffer: NettyPacketBuffer

    override fun decode(ctx: ChannelHandlerContext, source: ByteBuf, out: MutableList<Any>) {
        source.resetReaderIndex()
        println("[Decoder] Packet Hexdump: ${ByteBufUtil.hexDump(source)}")
        source.resetReaderIndex()
        println("[Decoder] Packet Size: ${source.readInt()}")
        println("[Decoder] Type: ${source.readShort()}")
        println("[Decoder] Nonce: ${source.readShort()}")
        source.resetReaderIndex()

        while (source.isReadable) {
            val currentLength = source.readableBytes()
            if (writing) {
                if (currentLength >= targetLength) {
                    writing = false
                    buffer.buf.writeBytes(source, targetLength)
                    targetLength = 0
                    out.add(NettyPacketBuffer.wrapped(buffer.buf.retainedDuplicate()))
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
                    out.add(NettyPacketBuffer.wrapped(buffer.buf.retainedDuplicate()))
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