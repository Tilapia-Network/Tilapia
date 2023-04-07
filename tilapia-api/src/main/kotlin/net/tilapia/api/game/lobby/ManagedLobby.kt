package net.tilapia.api.game.lobby

import net.tilapia.api.TilapiaCore
import net.tilapia.api.game.GameType

abstract class ManagedLobby(
    val core: TilapiaCore
): Lobby(core.provideGameId(GameType.LOBBY)) {



}