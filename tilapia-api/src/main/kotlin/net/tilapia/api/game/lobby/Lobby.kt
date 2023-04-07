package net.tilapia.api.game.lobby

import net.tilapia.api.game.Game
import net.tilapia.api.game.GameType
import java.util.*

abstract class Lobby(
    gameId: UUID, managed: Boolean
): Game(GameType.LOBBY, gameId, managed) {

}