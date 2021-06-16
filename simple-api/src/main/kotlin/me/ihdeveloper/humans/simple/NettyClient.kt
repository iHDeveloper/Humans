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
    internal var isActive: Boolean = false
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
            option(ChannelOption.TCP_NODELAY, true)
            handler(ClientInitializer())
        }

        val future = bootstrap.connect(host, port).sync()
        if (future.isSuccess) {
            isActive = true
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
                isActive = false
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