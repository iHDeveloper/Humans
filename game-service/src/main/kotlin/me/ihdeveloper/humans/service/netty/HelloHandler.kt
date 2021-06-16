package me.ihdeveloper.humans.service.netty

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.util.AttributeKey
import me.ihdeveloper.humans.service.protocol.PacketRegistry
import me.ihdeveloper.humans.service.protocol.response.PacketResponseHello

internal class HelloHandler : SimpleChannelInboundHandler<NettyPacketBuffer>() {
    companion object {
        private val helloKey = AttributeKey.newInstance<Boolean>("HELLO_KEY")
    }

    override fun handlerRemoved(context: ChannelHandlerContext) {
        println("[${context.channel().remoteAddress()}] Disconnected!")
    }

    override fun channelRead0(context: ChannelHandlerContext, source: NettyPacketBuffer) {
        val saidHello = context.channel().hasAttr(helloKey)

        if (!saidHello) {
            when (val packet = PacketRegistry.get(source)) {
                is PacketResponseHello -> {
                    context.channel().attr(helloKey).set(true)
                }
                else -> {
                    println("[${context.channel().remoteAddress()}] Hasn't said hello first! Kicking...")
                    context.close()
                    return
                }
            }
        }

        source.retain()
        source.buf.resetReaderIndex()
        context.fireChannelRead(source)
    }
}