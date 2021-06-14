package me.ihdeveloper.humans.simple.netty

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.ihdeveloper.humans.service.protocol.PacketBuffer
import me.ihdeveloper.humans.service.protocol.PacketRegistry
import me.ihdeveloper.humans.service.protocol.PacketResponseStatus
import me.ihdeveloper.humans.service.protocol.request.PacketRequestHello
import me.ihdeveloper.humans.service.protocol.request.PacketRequestPing
import me.ihdeveloper.humans.service.protocol.response.PacketResponseHello
import me.ihdeveloper.humans.simple.APIClient
import me.ihdeveloper.humans.simple.NettyClient
import me.ihdeveloper.humans.simple.apiScope
import me.ihdeveloper.humans.simple.logger

internal class PacketProcessor : SimpleChannelInboundHandler<PacketBuffer>() {
    private var saidHello = false

    override fun channelRead0(context: ChannelHandlerContext, source: PacketBuffer) {
        if (saidHello && source is NettyPacketBuffer) {
            val packet = PacketRegistry.get(source)
            if (packet is PacketRequestHello) {
                logger.info("Received hello! We are ready to go for the game!")
                val timeout = PacketRequestHello.readTimeout(source)
                apiScope.launch {
                    logger.info("Preparing the ping handler... (timeout: $timeout seconds)")
                    while (isActive) {
                        delay(timeout * 1000L)
                        withContext(Dispatchers.IO) {
                            val buffer = NettyPacketBuffer.alloc()
                            PacketRequestPing.write(buffer, -2)
                            NettyClient.send(buffer)
                        }
                    }
                }

                val response = NettyPacketBuffer.alloc()
                PacketResponseHello.write(response, packet.readNonce(source).toInt(), PacketResponseStatus.OK)
                context.writeAndFlush(response)
                source.release()
                saidHello = true
                return
            } else {
                source.buf.resetReaderIndex()
            }
        }

        apiScope.launch {
            APIClient.dispatch(source)
        }
    }
}