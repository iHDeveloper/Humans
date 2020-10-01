package me.ihdeveloper.humans.service

data class GameTime (
    var years: Int = 0,
    var months: Int  = 1,
    var days: Int  = 1,
    var hours: Int  = 1,
    var minutes: Int  = 0,
    var seconds: Int  = 0
) {
    override fun toString(): String {
        return "Year $years, Month ${x(months)}, Day ${x(days)} ${x(hours)}:${x(minutes)}:${x(seconds)}"
    }

    private fun x(x: Int): String {
        if (x < 10) return "0$x"
        return "" + x
    }
}

val gameTime = GameTime()

/**
 * Seconds per second = 5ms
 * Seconds per minute = 300ms
 * Seconds per hour = 18000ms (18s)
 * Seconds per day = 432000ms (7.2m)
 * Seconds per month = 12960000ms (3.6 hours)
 * Seconds per year = 155520000ms (1.8 day)
 *
 */
fun startTime() {
    println("[INFO] Game time started!")
    println("[INFO] Game Time: [$gameTime]")

    while(true) {
        gameTime.run {
            seconds++

            if (seconds >= 60) {
                seconds = 0
                minutes++

                if (minutes >= 60) {
                    minutes = 0
                    hours++
                    if (hours >= 24) {
                        hours = 0
                        days++
                        if (days >= 30) {
                            months++
                            if (months >= 12) {
                                months = 1
                                years++
                            }
                        }
                    }
                }

                println("[INFO] Game Time: [$gameTime]")
            }

            Thread.sleep(5)
        }
    }
}