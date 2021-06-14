package me.ihdeveloper.humans.service.netty

import io.netty.channel.ChannelDuplexHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.timeout.IdleState
import io.netty.handler.timeout.IdleStateEvent

internal class TimeoutHandler : ChannelDuplexHandler() {
    override fun userEventTriggered(channel: ChannelHandlerContext, event: Any) {
        if (event is IdleStateEvent) {
            val state = event.state()

            if (state === IdleState.READER_IDLE) {
                channel.close()
            }
        }
    }
}