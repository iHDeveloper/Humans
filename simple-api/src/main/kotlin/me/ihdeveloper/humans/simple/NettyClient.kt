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
            channel = future.channel()
            channel.closeFuture().sync()
        } finally {
            workerGroup.shutdownGracefully()
        }
    }

    fun send(msg: PacketBuffer) {
        channel.writeAndFlush(msg)
    }
}