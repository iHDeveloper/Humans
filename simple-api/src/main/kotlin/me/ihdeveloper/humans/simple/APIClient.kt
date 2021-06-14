package me.ihdeveloper.humans.simple

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.ihdeveloper.humans.service.protocol.PacketBuffer

object APIClient {
    private var listeners = mutableMapOf<Short, suspend (type: Short, source: PacketBuffer) -> Unit>()

    suspend fun call(message: PacketBuffer, nonce: Short, listener: suspend (type: Short, source: PacketBuffer) -> Unit) {
        listeners[nonce] = listener
        withContext(Dispatchers.IO) {
            NettyClient.send(message)
        }
    }

    suspend fun dispatch(message: PacketBuffer) {
        val type = message.readShort()
        val nonce = message.readShort()

        val listener = listeners[nonce] ?: return
        withContext(Dispatchers.Default) {
            listener.invoke(type, message)
        }
    }
}