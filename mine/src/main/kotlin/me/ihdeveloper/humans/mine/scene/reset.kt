package me.ihdeveloper.humans.mine.scene

import me.ihdeveloper.humans.core.Scene
import me.ihdeveloper.humans.core.util.GameLogger
import me.ihdeveloper.humans.mine.Mine

private const val SECOND = 20L

class NormalResetScene(
    mine: Mine
) : Scene("normal-reset", GameLogger("Scene/Reset/Normal")) {

    init {
        initFrame {
            mine.wizard.table.isLocked = true
        }

        frame(0) {
            mine.broadcastMessage("§7[Wizard] §cOscar: §eAuto reset has been invoked!")
        }

        frame(2 * SECOND) {
            mine.broadcastMessage("§7[Wizard] §cOscar: §cNo awards to the miners since they didn't finish the mine!")
        }

        disposeFrame {
            mine.reset()
            mine.wizard.table.isLocked = false
        }
    }

}
