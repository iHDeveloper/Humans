package me.ihdeveloper.humans.simple

import com.google.gson.Gson
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import me.ihdeveloper.humans.core.System
import me.ihdeveloper.humans.core.api.GameAPI
import me.ihdeveloper.humans.core.core
import me.ihdeveloper.humans.core.util.GameLogger
import me.ihdeveloper.humans.service.api.GameTime
import me.ihdeveloper.humans.service.api.Profile
import me.ihdeveloper.humans.service.protocol.PacketRegistry
import me.ihdeveloper.humans.service.protocol.PacketResponseStatus
import me.ihdeveloper.humans.service.protocol.request.PacketRequestProfile
import me.ihdeveloper.humans.service.protocol.request.PacketRequestTime
import me.ihdeveloper.humans.service.protocol.request.PacketRequestUpdateProfile
import me.ihdeveloper.humans.service.protocol.response.PacketResponseProfile
import me.ihdeveloper.humans.service.protocol.response.PacketResponseTime
import me.ihdeveloper.humans.service.protocol.response.PacketResponseUpdateProfile
import me.ihdeveloper.humans.simple.netty.NettyPacketBuffer
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

internal var apiScope: CoroutineScope = CoroutineScope(EmptyCoroutineContext)

internal val logger = GameLogger("API/Simple")
internal val gson = Gson()

@Suppress("UNUSED")
class Main : JavaPlugin() {
    companion object {
        internal lateinit var instance: Main
    }

    override fun onEnable() {
        instance = this
        core.api = SimpleAPI()
        core.otherSystems.add(APISystem())
        NettyClient.init("localhost", 80)
    }

    override fun onDisable() {
        if (apiScope.isActive)
            apiScope.cancel("Plugin disabled!")

        NettyClient.shutdown()
    }

    internal fun disable() {
        isEnabled = false
    }
}

class APISystem : System("Simple/API") {
    companion object {
        internal lateinit var plugin: JavaPlugin
    }

    override fun init(plugin: JavaPlugin) {
        Companion.plugin = plugin

        /** Register the plugin to be able to send outgoing messages to the BungeeCord */
        logger.info("Registering outgoing channel: BungeeCord")
        plugin.server.messenger.registerOutgoingPluginChannel(plugin, "BungeeCord")
    }

    override fun dispose() {
        logger.info("Cancelling the API coroutine scope...")
        apiScope.cancel("System is being disposed!")
    }
}


class SimpleAPI : GameAPI {
    private var lastNonce: Short = 1

    override fun getTime(block: (time: GameTime) -> Unit) {
        val buffer = NettyPacketBuffer.alloc()
        PacketRequestTime.write(buffer, lastNonce.toInt())
        apiScope.launch {
            APIClient.call(buffer, lastNonce) { type, source ->
                val packet = PacketRegistry.get(type)
                if (packet is PacketResponseTime) {
                    PacketResponseTime.skipStatus(source)
                    val time = PacketResponseTime.readTime(source)
                    Bukkit.getScheduler().runTask(Main.instance) {
                        block.invoke(time)
                    }
                }
            }
        }
        lastNonce++
    }

    override fun getProfile(name: String, block: (name: String, profile: Profile) -> Unit) {
        val buffer = NettyPacketBuffer.alloc()
        PacketRequestProfile.write(buffer, lastNonce.toInt(), name)
        apiScope.launch {
            APIClient.call(buffer, lastNonce) { type, source ->
                val packet = PacketRegistry.get(type)
                if (packet is PacketResponseProfile) {
                    PacketResponseProfile.skipStatus(source)
                    val profile = PacketResponseProfile.readProfile(source)
                    Bukkit.getScheduler().runTask(Main.instance) {
                        block.invoke(name, profile)
                    }
                }
            }
        }
        lastNonce++
    }

    override fun updateProfile(name: String, profile: Profile, block: (updated: Boolean) -> Unit) {
        val buffer = NettyPacketBuffer.alloc()
        PacketRequestUpdateProfile.write(buffer, lastNonce.toInt(), name, profile)
        apiScope.launch {
            APIClient.call(buffer, lastNonce) { type, source ->
                val packet = PacketRegistry.get(type)
                if (packet is PacketResponseUpdateProfile) {
                    val status = PacketResponseUpdateProfile.readStatus(source)
                    Bukkit.getScheduler().runTask(Main.instance) {
                        block.invoke(status === PacketResponseStatus.OK)
                    }
                }
            }
        }
        lastNonce++
    }


    override fun sendTo(player: Player, server: String) {
        val stream = ByteArrayOutputStream()
        val out = DataOutputStream(stream)
        out.run {
            writeUTF("Connect")
            writeUTF(server)
        }

        player.sendPluginMessage(APISystem.plugin, "BungeeCord", stream.toByteArray())
    }
}
