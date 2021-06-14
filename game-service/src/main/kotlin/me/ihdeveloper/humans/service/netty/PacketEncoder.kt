package me.ihdeveloper.humans.service.netty

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder

class PacketEncoder : MessageToByteEncoder<NettyPacketBuffer>() {
    override fun encode(ctx: ChannelHandlerContext, msg: NettyPacketBuffer, out: ByteBuf) {
        msg.buf.resetReaderIndex()
        out.writeBytes(msg.buf.readBytes(msg.buf.readableBytes()))
        msg.buf.release()
    }
}