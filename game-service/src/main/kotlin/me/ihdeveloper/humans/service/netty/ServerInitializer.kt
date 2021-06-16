package me.ihdeveloper.humans.service.netty

import io.netty.channel.Channel
import io.netty.channel.ChannelInitializer
import io.netty.handler.timeout.IdleStateHandler
import me.ihdeveloper.humans.service.InternalAPIHandler
import me.ihdeveloper.humans.service.protocol.request.PacketRequestHello

internal class ServerInitializer(
    private val handler: InternalAPIHandler
) : ChannelInitializer<Channel>() {

    override fun initChannel(channel: Channel) {
        val pipeline = channel.pipeline()

        pipeline.run {
            addLast(IdleStateHandler(30, 0, 0))
            addLast(TimeoutHandler())
            addLast(PacketDecoder(), PacketEncoder())
            addLast(HelloHandler(), PacketProcessor(handler))
        }

        println("[${channel.remoteAddress()}] Connected!")
        val buffer = NettyPacketBuffer.alloc()
        PacketRequestHello.write(buffer, 0, 5)
        channel.writeAndFlush(buffer)
    }

}