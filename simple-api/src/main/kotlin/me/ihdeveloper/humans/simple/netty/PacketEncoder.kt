package me.ihdeveloper.humans.simple.netty

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder

internal class PacketEncoder : MessageToByteEncoder<NettyPacketBuffer>() {
    override fun encode(context: ChannelHandlerContext, input: NettyPacketBuffer, out: ByteBuf) {
        input.buf.run {
            resetReaderIndex()
            out.writeBytes(this.readBytes(this.readableBytes()))
            release()
        }
    }
}