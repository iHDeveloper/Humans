package me.ihdeveloper.humans.core.util

import me.ihdeveloper.humans.core.corePlugin
import org.bukkit.Bukkit
import org.bukkit.entity.Player

// TODO requires rewrite
class Conversation(
    private val player: Player,
    private val messages: Array<String>
): Runnable {
    private var index = 0

    fun start() = sendChat()

    override fun run() = sendChat()

    private fun sendChat() {
        if (index >= messages.size) {
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