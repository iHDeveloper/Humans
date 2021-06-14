package me.ihdeveloper.humans.simple

import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel
import me.ihdeveloper.humans.service.protocol.PacketBuffer
import me.ihdeveloper.humans.simple.netty.ClientInitializer

object NettyClient {
    private lateinit var channel: Channel

    fun init(host: String, port: Int) {
        logger.info("Connecting to the game service...")
        val workerGroup = NioEventLoopGroup()

        try {
            val bootstrap = Bootstrap()

            bootstrap.run {
                group(workerGroup)
                channel(NioSocketChannel::class.java)
                option(ChannelOption.SO_KEEPALIVE, true)
                handler(ClientInitializer())
            }

            val future = bootstrap.connect(host, port).sync()
            if (future.isSuccess) {
                channel = future.channel()
            } else {
                future.cause().printStackTrace()
                Main.instance.disable()
                error("Failed to connect to the game service server!")
            }
        } finally {
            workerGroup.shutdownGracefully()
        }
    }

    fun shutdown() {
        synchronized(channel) {
            channel.close().sync()
        }
    }

    fun send(msg: PacketBuffer) {
        channel.writeAndFlush(msg)
    }
}