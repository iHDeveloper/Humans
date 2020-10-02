@file:JvmName("Main")
package me.ihdeveloper.humans.service

import com.google.gson.Gson
import me.ihdeveloper.humans.service.api.Profile
import spark.Request
import spark.Response
import spark.Spark.*
import kotlin.concurrent.thread

val profiles = mutableMapOf<String, Profile>()

val gson = Gson()

fun Profile.serialize(): String {
    return gson.toJson(this)
}

fun GameTime.serialize(): String {
    return gson.toJson(this)
}

fun Profile.Companion.deserialize(json: String): Profile {
    return gson.fromJson(json, Profile::class.java)
}

fun main() {
    println("[INFO] Initializing the game service...")
    thread {
        gameTime.start(true)
    }

    port(80)
    initExceptionHandler { e: java.lang.Exception? -> e?.printStackTrace() }

    println("[INFO] Game service started!")
    println("[INFO] Listening on port 80...")

    /** Read the current time of the game */
    get("/time", fun(_: Request, res: Response): String {
        res.status(200)
        return gameTime.serialize()
    })

    /** Read a profile from the map */
    get("/profile/:name", fun(req: Request, res: Response): String {
        val name = req.params("name")

        val profile = profiles[name]

        if (profile == null) {
            res.status(204)
            return "{}"
        }

        return profile.serialize()
    })

    /** Update profile in the map */
    post("/profile/:name", fun(req: Request, res: Response) {
        val name = req.params("name")

        if (profiles[name] == null) {
            res.status(404)
            return
        }

        try {
            profiles[name] = Profile.deserialize(req.body())
            res.status(204)
        } catch (e: Exception) {
            res.status(406)
        }
    })
}