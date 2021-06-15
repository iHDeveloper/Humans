package me.ihdeveloper.humans.service

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.logging.LogLevel
import io.netty.handler.logging.LoggingHandler
import me.ihdeveloper.humans.service.netty.ServerInitializer
import me.ihdeveloper.humans.service.protocol.PacketRegistry

object NettyServer {
    internal var port = 2500
    internal lateinit var handler: InternalAPIHandler

    fun init() {
        PacketRegistry.init()

        val bossGroup = NioEventLoopGroup(1)
        val workerGroup = NioEventLoopGroup()

        try {
            val bootstrap = ServerBootstrap()

            bootstrap.run {
                group(bossGroup, workerGroup)
                channel(NioServerSocketChannel::class.java)
                handler(LoggingHandler(LogLevel.DEBUG))
                option(ChannelOption.SO_BACKLOG, 128)
                childHandler(ServerInitializer(handler))
                childOption(ChannelOption.SO_KEEPALIVE, true)
                childOption(ChannelOption.TCP_NODELAY, true)
            }

            val future = bootstrap.bind(port).sync()

            future.channel().closeFuture().sync()
        } finally {
            bossGroup.shutdownGracefully()
            workerGroup.shutdownGracefully()
        }
    }
}