package me.ihdeveloper.humans.service.netty

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler

internal class PacketReleaser : SimpleChannelInboundHandler<NettyPacketBuffer>() {
    override fun channelRead0(context: ChannelHandlerContext, msg: NettyPacketBuffer) {
        msg.buf.release()
    }
}