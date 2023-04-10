package net.tilapiamc.api.game.lobby

import net.tilapiamc.api.game.Game
import net.tilapiamc.api.game.GameType
import net.tilapiamc.api.server.TilapiaServer
import java.util.*

abstract class Lobby(
    server: TilapiaServer, gameId: UUID, managed: Boolean, val lobbyType: String
): Game(server, GameType.LOBBY, gameId, managed) {

}