@file:JvmName("Main")
package me.ihdeveloper.humans.service

import com.google.gson.Gson
import kotlin.concurrent.thread
import me.ihdeveloper.humans.service.api.Profile
import me.ihdeveloper.humans.service.api.Project
import me.ihdeveloper.humans.service.api.ProjectItem
import reactor.core.publisher.Mono
import reactor.netty.http.server.HttpServer

val profiles = mutableMapOf<String, Profile>()

val gson = Gson()

internal fun Profile.serialize(): String {
    return gson.toJson(this)
}

internal fun GameTime.serialize(): String {
    return gson.toJson(this)
}

internal fun Profile.Companion.deserialize(json: String): Profile {
    return gson.fromJson(json, Profile::class.java)
}

/** Prison Projects */
internal val materialShopProject = Project(
    "material-shop",
    "§eMaterial Shop",
    "§eA shop for selling natural materials",
    arrayOf(
        "§7A place where \"Humans\" sells natural materials such as §fPrison Stone",
        "§7And, other \"Humans\" for buying them"
    ),
    arrayOf(
        ProjectItem(
            "§eStone Brick",
            arrayOf("§7Used for decorating the building with the prison's style"),
            "BRICK",
            0,
            "prison:enchanted:stone",
            32,
            1000
        )
    )
)

fun main() {
    println("[INFO] Initializing the game service...")
    thread {
        gameTime.start(true)
    }

    println("[INFO] Game service started!")
    println("[INFO] Listening on port 80...")

    val server = HttpServer.create().apply {
        port(80)

        /* Routing */
        route {

            /* Read the current time of the game */
            it.get("/time") { _, res ->
                res.sendString(Mono.just(gameTime.serialize()))
            }

            /* Read a profile from the map */
            it.get("/profile/{name}") { req, res ->
                val name = req.param("name")

                val profile = profiles[name]

                if (profile == null) {
                    res.sendString(Mono.just("{}"))
                } else {
                    res.sendString(Mono.just(profile.serialize()))
                }
            }

            it.post("/profile/{name}") { req, res ->
                val name = req.param("name")

                val flux = req.receive().retain()

                flux.asString().map { data ->
                    try {
                        profiles[name] = Profile.deserialize(data)
                        res.status(204)
                    } catch (e: Exception) {
                        res.status(406)
                    }
                }
                res.send()
            }
        }

    }.bindNow()

    server.onDispose().block()
}
