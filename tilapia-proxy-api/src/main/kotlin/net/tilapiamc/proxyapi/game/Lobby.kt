package net.tilapiamc.proxyapi.game

import net.tilapiamc.proxyapi.servers.TilapiaServer
import java.util.*

abstract class Lobby(
    server: TilapiaServer, gameId: UUID, managed: Boolean, val lobbyType: String
): Game(server, GameType.LOBBY, gameId, managed) {

    override fun equals(other: Any?): Boolean {
        return other is Lobby && other.gameId == gameId
    }

}