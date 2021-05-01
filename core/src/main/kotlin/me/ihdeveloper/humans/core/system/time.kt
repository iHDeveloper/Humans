package me.ihdeveloper.humans.core.system

import me.ihdeveloper.humans.core.System
import me.ihdeveloper.humans.core.core
import me.ihdeveloper.spigot.devtools.api.DevTools
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scoreboard.DisplaySlot

private const val SCORE_DATE = "§1§8» §eDate "
private const val SCORE_DAY = "§2§8» §eDay "
private const val SCORE_TIME = "§3"
private const val TEAM_DATE = "@date"
private const val TEAM_DAY = "@day"
private const val TEAM_TIME = "@time"

private const val FULL_MC_DAY = 24000L
private const val FULL_MC_HOUR = 1000L

/**
 * A system for handling the game time in the server
 */
class TimeSystem : System("Core/Time"), Runnable {

    override fun init(plugin: JavaPlugin) {
        Bukkit.getScheduler().runTaskTimer(plugin, this, 0L, 20L)
    }

    override fun run() {
        val gameTime = core.time
        val worldTime = gameTime.let {
            // TODO Calculate the minutes for more smooth timing
            (FULL_MC_DAY * it.days) + ( if (it.hours >= 6) FULL_MC_HOUR * (it.hours - 6) else FULL_MC_HOUR * (18 + it.hours))
        }

        Bukkit.getWorlds().forEach {
            it.weatherDuration = 0
            it.thunderDuration = 0
            it.isThundering = false
            it.fullTime = worldTime
        }

        val years = x(gameTime.years)
        val months = x(gameTime.months)
        val am = if (gameTime.hours >= 12) "PM" else "AM"
        val hours = x(if (gameTime.hours >= 12) gameTime.hours - 12 else gameTime.hours)
        val minutes = x(5 * (gameTime.minutes / 5))

        DevTools.watch("Game Time", "${core.time}")

        Bukkit.getOnlinePlayers().forEach {

            it.scoreboard.apply {
                val objective = getObjective(DisplaySlot.SIDEBAR)

                objective.getScore("§9§9").score = 9
                val date = getTeam(TEAM_DATE) ?: registerNewTeam(TEAM_DATE).apply {
                    addEntry(SCORE_DATE)

                    objective.getScore(SCORE_DATE).score = 8
                }

                val day = getTeam(TEAM_DAY) ?: registerNewTeam(TEAM_DAY).apply {
                    addEntry(SCORE_DAY)

                    objective.getScore(SCORE_DAY).score = 8
                }

                val time = getTeam(TEAM_TIME) ?: registerNewTeam(TEAM_TIME).apply {
                    addEntry(SCORE_TIME)

                    objective.getScore(SCORE_TIME).score = 8
                }

                date.suffix = "§f${years}§8/§f${months}"
                day.suffix = "§f${gameTime.days}"
                time.prefix = "§8» §f${hours}"
                time.suffix = "§6:§f${minutes} §e${am}"
            }
        }
    }

    override fun dispose() {}

    private fun x(x: Int): String = if (x < 10) "0$x" else "" + x
}
