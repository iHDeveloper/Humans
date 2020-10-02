package me.ihdeveloper.humans.blocks

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResult
import com.google.gson.Gson
import kotlinx.coroutines.*
import me.ihdeveloper.humans.core.GameLogger
import me.ihdeveloper.humans.core.api.GameAPI
import me.ihdeveloper.humans.core.core
import me.ihdeveloper.humans.service.GameTime
import me.ihdeveloper.humans.service.api.Profile
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

val logger = GameLogger("API/Blocks")
val gson = Gson()

//const val API_ENDPOINT = "humans.blocks.api"
const val API_ENDPOINT = "http://localhost"

class Main : JavaPlugin() {
    override fun onEnable() {
        core.api = BlocksAPI()
    }

    override fun onDisable() {}
}

class BlocksAPI : GameAPI {
    override fun getTime(): GameTime {
        return runBlocking {
            logger.info("Fetching the game time...")

            /** Reference to the default time in the core */
            var time = core.time

            Fuel.get("$API_ENDPOINT/time").awaitStringResult().fold(
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

    override fun getProfile(player: Player): Profile {
        return runBlocking {
            val req = Fuel.get("$API_ENDPOINT/profile/${player.name}")
            val data = req.body.toString()

            gson.fromJson(data, Profile::class.java)
        }
    }

    override fun updateProfile(player: Player) {
        TODO("Not yet implemented")
    }
}
