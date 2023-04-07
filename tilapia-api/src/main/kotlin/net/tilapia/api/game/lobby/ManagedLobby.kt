package net.tilapia.api.game.lobby

import net.tilapia.api.TilapiaCore
import net.tilapia.api.game.GameType

abstract class ManagedLobby(
    val core: TilapiaCore
): Lobby(core.getLocalServer(), core.provideGameId(GameType.LOBBY), true) {

    fun end() {
        core.removeGame(this)
    }

}