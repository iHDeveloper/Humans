@file:JvmName("Main")
package me.ihdeveloper.humans.service

import com.google.gson.Gson
import kotlin.concurrent.thread
import me.ihdeveloper.humans.service.api.Profile
import me.ihdeveloper.humans.service.api.Project
import me.ihdeveloper.humans.service.api.ProjectItem
import spark.Request
import spark.Response
import spark.Spark.get
import spark.Spark.initExceptionHandler
import spark.Spark.port
import spark.Spark.post

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
            res.status(200)
            return "{}"
        }

        return profile.serialize()
    })

    /** Update profile in the map */
    post("/profile/:name", fun(req: Request, res: Response) {
        val name = req.params("name")

        try {
            profiles[name] = Profile.deserialize(req.body())
            res.status(204)
        } catch (e: Exception) {
            res.status(406)
        }
    })

    /** Read information about project */
//    get("/project/:name". fun(req: Request, res: Response) { "" })
}