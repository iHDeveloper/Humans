package me.ihdeveloper.humans.simple.netty

import io.netty.buffer.ByteBufUtil
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.ihdeveloper.humans.service.protocol.PacketRegistry
import me.ihdeveloper.humans.service.protocol.request.PacketRequestHello
import me.ihdeveloper.humans.service.protocol.request.PacketRequestPing
import me.ihdeveloper.humans.service.protocol.response.PacketResponseHello
import me.ihdeveloper.humans.simple.APIClient
import me.ihdeveloper.humans.simple.NettyClient
import me.ihdeveloper.humans.simple.apiScope
import me.ihdeveloper.humans.simple.logger

internal class PacketProcessor : SimpleChannelInboundHandler<NettyPacketBuffer>() {

    override fun channelRead0(context: ChannelHandlerContext, source: NettyPacketBuffer) {
        logger.debug("Packet Hexdump: ${ByteBufUtil.hexDump(source.buf)}")
        source.buf.resetReaderIndex()

        val packet = PacketRegistry.get(source)
        if (packet is PacketRequestHello) {
            val nonce = packet.readNonce(source).toInt()
            val timeout = packet.readTimeout(source)
            val response = NettyPacketBuffer.alloc()
            apiScope.launch {
                logger.info("Preparing the ping handler... (timeout: $timeout seconds)")
                while (isActive) {
                    delay(timeout * 1000L)
                    logger.info("Sending ping to game service server!")
                    withContext(Dispatchers.IO) {
                        val buffer = NettyPacketBuffer.alloc()
                        PacketRequestPing.write(buffer, -2)
                        NettyClient.send(buffer)
                    }
                }
            }
            source.retain()
            context.fireChannelRead(source)
            source.release()
            PacketResponseHello.write(response, nonce, "simple-api")
            context.channel().writeAndFlush(response)
            return
        } else {
            source.buf.resetReaderIndex()
        }

        apiScope.launch {
            APIClient.dispatch(source)
        }
    }

    override fun exceptionCaught(context: ChannelHandlerContext, cause: Throwable) {
        logger.error("Something wrong happened with the game service connection!")
        cause.printStackTrace()
    }
}