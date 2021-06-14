package me.ihdeveloper.humans.service.netty

import io.netty.buffer.ByteBufUtil
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import me.ihdeveloper.humans.service.APIHandler
import me.ihdeveloper.humans.service.protocol.PacketBuffer
import me.ihdeveloper.humans.service.protocol.PacketRegistry
import me.ihdeveloper.humans.service.protocol.PacketResponseStatus
import me.ihdeveloper.humans.service.protocol.request.PacketRequestPing
import me.ihdeveloper.humans.service.protocol.request.PacketRequestProfile
import me.ihdeveloper.humans.service.protocol.request.PacketRequestTime
import me.ihdeveloper.humans.service.protocol.request.PacketRequestUpdateProfile
import me.ihdeveloper.humans.service.protocol.response.PacketResponsePing
import me.ihdeveloper.humans.service.protocol.response.PacketResponseProfile
import me.ihdeveloper.humans.service.protocol.response.PacketResponseTime
import me.ihdeveloper.humans.service.protocol.response.PacketResponseUpdateProfile

internal class PacketProcessor(
    private val handler: APIHandler
) : SimpleChannelInboundHandler<PacketBuffer>() {
    override fun channelRead0(ctx: ChannelHandlerContext, source: PacketBuffer) {
        when (val packet = PacketRegistry.get(source)) {
            is PacketRequestPing -> {
                val buffer = NettyPacketBuffer.alloc()
                PacketResponsePing.write(buffer, packet.readNonce(source).toInt())
                ctx.writeAndFlush(buffer)
            }
            is PacketRequestTime -> {
                val time = handler.getTime()
                val buffer = NettyPacketBuffer.alloc()
                PacketResponseTime.write(buffer, packet.readNonce(source).toInt(), time)
                ctx.writeAndFlush(buffer)
            }
            is PacketRequestProfile -> {
                val name = packet.readName(source)
                val response = handler.getProfile(name)
                val buffer = NettyPacketBuffer.alloc()
                PacketResponseProfile.write(buffer, packet.readNonce(source).toInt(), response)
                ctx.writeAndFlush(buffer)
            }
            is PacketRequestUpdateProfile -> {
                val name = packet.readName(source)
                val profile = packet.readProfile(source)
                val response: PacketResponseStatus = if (handler.updateProfile(name, profile)) PacketResponseStatus.OK else PacketResponseStatus.NOT_FOUND
                val buffer = NettyPacketBuffer.alloc()
                PacketResponseUpdateProfile.write(buffer, packet.readNonce(source).toInt(), response)
                ctx.writeAndFlush(buffer)
            }
            else -> {
                println("Packet Buffer Hex Dump:")
                if (source is NettyPacketBuffer) {
                    println(ByteBufUtil.hexDump(source.buf))
                    source.buf.release()
                }
                error("Unable to process unknown packet!")
            }
        }
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        println("Failed to parse a packet! Closing the channel...")
        cause.printStackTrace()
        ctx.close()
    }
}