package me.ihdeveloper.humans.simple

import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelOption
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel
import me.ihdeveloper.humans.service.protocol.PacketBuffer
import me.ihdeveloper.humans.service.protocol.PacketRegistry
import me.ihdeveloper.humans.simple.netty.ClientInitializer

object NettyClient {
    private lateinit var channel: Channel
    private lateinit var workerGroup: EventLoopGroup

    fun init(host: String, port: Int) {
        PacketRegistry.init()
        logger.info("Connecting to the game service...")
        workerGroup = NioEventLoopGroup()

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
            workerGroup.shutdownGracefully()
            Main.instance.disable()
            error("Failed to connect to the game service server!")
        }
    }

    fun shutdown() {
        if (channel.isActive) {
            try {
                channel.close().sync()
            } finally {
                workerGroup.shutdownGracefully()
            }
        }
    }

    fun send(msg: PacketBuffer) {
        channel.writeAndFlush(msg)
    }
}