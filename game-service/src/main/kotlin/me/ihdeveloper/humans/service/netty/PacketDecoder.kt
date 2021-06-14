package me.ihdeveloper.humans.service.netty

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder

class PacketDecoder : ByteToMessageDecoder() {
    override fun decode(ctx: ChannelHandlerContext, `in`: ByteBuf, out: MutableList<Any>) {
        out.add(NettyPacketBuffer.wrapped(`in`.readBytes(`in`.readableBytes())))
    }
}