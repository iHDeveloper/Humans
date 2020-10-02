package me.ihdeveloper.humans.core.system

import me.ihdeveloper.humans.core.System
import me.ihdeveloper.humans.core.core
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scoreboard.DisplaySlot

const val SCORE_DATE = "§0 §8» §eDate: "
const val SCORE_DAY = "§1 §8» §eDay "
const val SCORE_TIME = "§2"
const val TEAM_DATE = "@date"
const val TEAM_DAY = "@day"
const val TEAM_TIME = "@time"

/**
 * A system for handling the game time in the server
 */
class TimeSystem : System("Core/Time"), Runnable {

    override fun init(plugin: JavaPlugin) {
        Bukkit.getScheduler().runTaskTimer(plugin, this, 0L, 20L)
    }

    override fun run() {
        val gameTime = core.time
        val years = x(gameTime.years)
        val months = x(gameTime.months)
        val am = if (gameTime.hours >= 12) "PM" else "AM"
        val hours = x(gameTime.hours)
        val minutes = x(gameTime.minutes)

        Bukkit.getOnlinePlayers().forEach {
            it.scoreboard.apply {
                val objective = getObjective(DisplaySlot.SIDEBAR) ?: registerNewObjective("sidebar", "dummy").apply {
                    displayName = "§e§lTHE HUMANS"
                    displaySlot = DisplaySlot.SIDEBAR
                }

                val date = getTeam(TEAM_DATE) ?: registerNewTeam(TEAM_DATE).apply {
                    addEntry(SCORE_DATE)

                    objective.getScore(SCORE_DATE).score = 9
                }

                val day = getTeam(TEAM_DAY) ?: registerNewTeam(TEAM_DAY).apply {
                    addEntry(SCORE_DAY)

                    objective.getScore(SCORE_DAY).score = 8
                }

                val time = getTeam(TEAM_TIME) ?: registerNewTeam(TEAM_TIME).apply {
                    addEntry(SCORE_TIME)

                    objective.getScore(SCORE_TIME).score = 7
                }

                date.suffix = "§f${years}§8/§f${months}"
                day.suffix = "§f${gameTime.days}"
                time.prefix = "§8 » §f${hours}"
                time.suffix = "§6:§f${minutes} §e${am}"
            }
        }
    }

    override fun dispose() {}

    private fun x(x: Int): String = if (x < 10) "0$x" else "" + x
}
