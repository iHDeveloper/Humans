package me.ihdeveloper.humans.simple.netty

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder

internal class PacketDecoder : ByteToMessageDecoder() {
    override fun decode(context: ChannelHandlerContext, input: ByteBuf, out: MutableList<Any>) {
        out.add(NettyPacketBuffer.wrapped(input.readBytes(input.readableBytes())))
    }
}