package me.ihdeveloper.humans.service.netty

import io.netty.buffer.ByteBufUtil
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.util.AttributeKey
import me.ihdeveloper.humans.service.APIHandler
import me.ihdeveloper.humans.service.protocol.PacketBuffer
import me.ihdeveloper.humans.service.protocol.PacketRegistry
import me.ihdeveloper.humans.service.protocol.PacketResponseStatus
import me.ihdeveloper.humans.service.protocol.request.PacketRequestPing
import me.ihdeveloper.humans.service.protocol.request.PacketRequestProfile
import me.ihdeveloper.humans.service.protocol.request.PacketRequestTime
import me.ihdeveloper.humans.service.protocol.request.PacketRequestUpdateProfile
import me.ihdeveloper.humans.service.protocol.response.PacketResponseHello
import me.ihdeveloper.humans.service.protocol.response.PacketResponsePing
import me.ihdeveloper.humans.service.protocol.response.PacketResponseProfile
import me.ihdeveloper.humans.service.protocol.response.PacketResponseTime
import me.ihdeveloper.humans.service.protocol.response.PacketResponseUpdateProfile

internal class PacketProcessor(
    private val handler: APIHandler
) : SimpleChannelInboundHandler<PacketBuffer>() {
    private val nameAttr = AttributeKey.newInstance<String>("NAME")

    override fun channelRead0(context: ChannelHandlerContext, source: PacketBuffer) {
        val nickname = if (context.channel().hasAttr(nameAttr)) context.channel().attr(nameAttr).get() else context.channel().remoteAddress().toString()

        when (val packet = PacketRegistry.get(source)) {
            is PacketResponseHello -> {
                packet.skipStatus(source)
                val name = packet.readName(source)
                context.channel().attr(nameAttr).set(name)
                println("[./${context.channel().remoteAddress()}] has been identified as $name")
            }
            is PacketRequestPing -> {
                val buffer = NettyPacketBuffer.alloc()
                PacketResponsePing.write(buffer, packet.readNonce(source).toInt())
                println("[$nickname] Pinged! replying with pong...")
                context.writeAndFlush(buffer)
            }
            is PacketRequestTime -> {
                val time = handler.getTime()
                val buffer = NettyPacketBuffer.alloc()
                PacketResponseTime.write(buffer, packet.readNonce(source).toInt(), time)
                println("[$nickname] Requested time! replying with time... (current: $time)")
                context.writeAndFlush(buffer)
            }
            is PacketRequestProfile -> {
                val name = packet.readName(source)
                val response = handler.getProfile(name)
                val buffer = NettyPacketBuffer.alloc()
                PacketResponseProfile.write(buffer, packet.readNonce(source).toInt(), response)
                println("[$nickname] Requested profile with name $name! replying with profile data...")
                context.writeAndFlush(buffer)
            }
            is PacketRequestUpdateProfile -> {
                val name = packet.readName(source)
                val profile = packet.readProfile(source)
                val response: PacketResponseStatus = if (handler.updateProfile(name, profile)) PacketResponseStatus.OK else PacketResponseStatus.NOT_FOUND
                val buffer = NettyPacketBuffer.alloc()
                PacketResponseUpdateProfile.write(buffer, packet.readNonce(source).toInt(), response)
                println("[$nickname] Requested to update profile with name $name! replying with update status... (status: $response)")
                context.writeAndFlush(buffer)
            }
            else -> {
                println("[$nickname] Sent an unknown packet...")
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