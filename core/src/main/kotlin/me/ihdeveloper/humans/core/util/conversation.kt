package me.ihdeveloper.humans.core.util

import me.ihdeveloper.humans.core.corePlugin
import org.bukkit.Bukkit
import org.bukkit.entity.Player

// TODO requires rewrite
class Conversation(
    private val player: Player,
    private val messages: Array<String>
): Runnable {
    companion object {
        val currentRunning = mutableMapOf<String, Conversation>()
    }

    private var index = 0
    private var stop = false

    fun start() {
        val current = currentRunning[player.name]
        current?.stop()

        currentRunning[player.name] = this
        sendChat()
    }

    fun stop() {
        stop = true
        currentRunning.remove(player.name)
    }

    override fun run() = sendChat()

    private fun sendChat() {
        if (stop) {
            return
        }

        if (index >= messages.size) {
            currentRunning.remove(player.name)
            return
        }

        player.sendMessage(messages[index])
        index++
        schedule()
    }

    private fun schedule() {
        Bukkit.getScheduler().runTaskLater(corePlugin, this, 2 * 20L)
    }

}