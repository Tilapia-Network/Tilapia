package net.tilapia.api.game.lobby

import net.tilapia.api.game.Game
import net.tilapia.api.game.GameType
import net.tilapia.api.server.TilapiaServer
import java.util.*

abstract class Lobby(
    server: TilapiaServer, gameId: UUID, managed: Boolean
): Game(server, GameType.LOBBY, gameId, managed) {

}