@file:JvmName("Main")
package me.ihdeveloper.humans.service

import kotlin.concurrent.thread
import me.ihdeveloper.humans.service.api.GameTime

internal val gameTime = GameTime()
private val internalAPIHandler = InternalAPIHandler(gameTime)

fun main() {
    println("[INFO] Initializing...")
    println("Starting the game time")
    thread {
        gameTime.start(true)
    }

    println("[INFO] Initializing Netty server on port 80...")
    NettyServer.handler = internalAPIHandler
    NettyServer.port = 80
    NettyServer.init()
}
