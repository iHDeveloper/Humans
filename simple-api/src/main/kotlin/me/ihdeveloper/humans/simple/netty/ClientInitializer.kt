package me.ihdeveloper.humans.simple.netty

import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInitializer

internal class ClientInitializer : ChannelInitializer<Channel>() {
    override fun initChannel(channel: Channel) {
        me.ihdeveloper.humans.simple.logger.info("Connected to the game service!")
        val pipeline = channel.pipeline()

        pipeline.run {
            addLast(PacketDecoder(), PacketEncoder())
            addLast(PacketProcessor(), SafeOutputQueueProcessor())
        }
    }

    override fun exceptionCaught(context: ChannelHandlerContext, cause: Throwable) {
        me.ihdeveloper.humans.simple.logger.error("Something wrong happened with the game service connection!")
        cause.printStackTrace()
    }
}