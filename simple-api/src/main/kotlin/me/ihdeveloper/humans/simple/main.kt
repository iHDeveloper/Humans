package me.ihdeveloper.humans.simple

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResult
import com.google.common.io.ByteStreams
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import me.ihdeveloper.humans.core.System
import me.ihdeveloper.humans.core.api.GameAPI
import me.ihdeveloper.humans.core.core
import me.ihdeveloper.humans.core.util.GameLogger
import me.ihdeveloper.humans.service.GameTime
import me.ihdeveloper.humans.service.api.Profile
import me.ihdeveloper.humans.service.api.Skills
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

val logger = GameLogger("API/Simple")
val gson = Gson()

const val API_ENDPOINT = "http://localhost"

const val PROFILE_TIMEOUT = 50

@Suppress("UNUSED")
class Main : JavaPlugin() {
    override fun onEnable() {
        core.api = SimpleAPI()
        core.otherSystems.add(APISystem())
    }

    override fun onDisable() {}
}

class APISystem : System("Simple/API") {
    companion object {
        internal lateinit var plugin: JavaPlugin
    }

    override fun init(plugin: JavaPlugin) {
        Companion.plugin = plugin

        /** Register the plugin to be able to send outgoing messages to the BungeeCord */
        plugin.server.messenger.registerOutgoingPluginChannel(plugin, "BungeeCord")
        logger.info("Registering outgoing channel: BungeeCord")
    }

    override fun dispose() {}
}


class SimpleAPI : GameAPI {
    override fun getTime(): GameTime {
        return runBlocking {
            logger.info("Fetching the game time...")

            /** Reference to the default time in the core */
            var time = core.time

            Fuel.get("$API_ENDPOINT/time")
                .awaitStringResult().fold(
                { data ->
                    time = gson.fromJson(data, GameTime::class.java)
                    logger.info("Fetched! Game Time -> $time")
                },
                { err ->
                    logger.error("An error of type ${err.exception} happened: ${err.message}")
                }
            )

            time
        }
    }

    override fun getProfile(name: String): Profile? {
        return runBlocking {
            logger.info("Fetching profile/${name}...")

            Fuel.get("$API_ENDPOINT/profile/$name")
                .timeout(PROFILE_TIMEOUT)
                .timeoutRead(PROFILE_TIMEOUT)
                .awaitStringResult()
                .fold(
                    { data ->
                        if (data == "{}") {
                            Profile(
                                skills = Skills(),
                                inventory = mapOf(),
                                new = true
                            )
                        } else {
                            logger.info("Fetched! profile/${name}...")
                            gson.fromJson(data, Profile::class.java)
                        }
                    },
                    { err ->
                        logger.error("An error of type ${err.exception} happened: ${err.message}")
                        null
                    }
                )
        }
    }

    override fun updateProfile(name: String, profile: Profile) {
        return runBlocking {
            logger.info("Updating profile/$name...")

            Fuel.post("$API_ENDPOINT/profile/$name")
//                .timeout(PROFILE_TIMEOUT)
//                .timeoutRead(PROFILE_TIMEOUT)
                .body(gson.toJson(profile))
                .awaitStringResult()
                .fold(
                    {
                        /** Successfully updated */
                        logger.info("Updated! profile/$name")
                    },
                    { err ->
                        logger.error("Failed to update profile/$name")
                        logger.error("An error of type ${err.exception} happened: ${err.message}")
                    }
                )

        }
    }

    override fun sendTo(player: Player, server: String) {
        val output = ByteStreams.newDataOutput().apply {
            writeUTF("Connect")
            writeUTF(server)
        }

        player.sendPluginMessage(APISystem.plugin, "BungeeCord", output.toByteArray())
    }
}