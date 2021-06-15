package me.ihdeveloper.humans.simple.netty

import io.netty.channel.ChannelDuplexHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelPromise
import io.netty.util.AttributeKey
import me.ihdeveloper.humans.service.protocol.PacketRegistry
import me.ihdeveloper.humans.service.protocol.request.PacketRequestHello
import me.ihdeveloper.humans.service.protocol.response.PacketResponseHello
import me.ihdeveloper.humans.simple.logger

internal class SafeOutputQueueProcessor : ChannelDuplexHandler() {
    companion object {
        internal val helloKey = AttributeKey.valueOf<Boolean>("HELLO_KEY")
    }
    private val buffer = ArrayDeque<NettyPacketBuffer>()

    override fun handlerAdded(context: ChannelHandlerContext) {
        logger.debug("Waiting to receive messages to safe the protocol...")
        context.channel().attr(helloKey).set(false)
    }

    override fun channelRead(context: ChannelHandlerContext, input: Any) {
        val hasSaidHello = context.channel().attr(helloKey).get()

        if (!hasSaidHello && input is NettyPacketBuffer) {
            input.buf.resetReaderIndex()
            val packet = PacketRegistry.get(input)
            if (packet is PacketRequestHello) {
                logger.debug("Caught the hello request! Freeing up all the prisoners in the queue buffer...")
                context.channel().attr(helloKey).set(true)
                while (buffer.isNotEmpty()) {
                    val cached = buffer.removeFirst()
                    context.channel().writeAndFlush(cached)
                    cached.release()
                }
            }
        }
    }

    override fun write(context: ChannelHandlerContext, output: Any, promise: ChannelPromise) {
        val hasSaidHello = context.channel().attr(helloKey).get()

        if (!hasSaidHello && output is NettyPacketBuffer) {
            output.buf.resetReaderIndex()
            val packet = PacketRegistry.get(output)
            if (packet !is PacketResponseHello) {
                logger.debug("Caught unsafe message trying to bypass! adding to the queue buffer... ($packet)")
                output.buf.resetReaderIndex()
                output.buf.retain()
                buffer.add(output)
                return
            }
            logger.debug("Caught hello being sent... it seems like we are good to go!")
            output.buf.resetReaderIndex()
            context.channel().attr(helloKey).set(true)
        }

        super.write(context, output, promise)
    }

    override fun flush(context: ChannelHandlerContext) {
        if (context.channel().attr(helloKey).get()) {
            super.flush(context)
        }
    }

    override fun exceptionCaught(context: ChannelHandlerContext, cause: Throwable) {
        cause.printStackTrace()
    }
}