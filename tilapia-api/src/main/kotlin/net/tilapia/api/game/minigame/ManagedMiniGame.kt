package net.tilapia.api.game.minigame

import net.tilapia.api.TilapiaCore
import net.tilapia.api.game.GameType

abstract class ManagedMiniGame(
    val core: TilapiaCore
): MiniGame(core.getLocalServer(), core.provideGameId(GameType.MINIGAME), true) {

    fun end() {
        core.removeGame(this)
    }

}