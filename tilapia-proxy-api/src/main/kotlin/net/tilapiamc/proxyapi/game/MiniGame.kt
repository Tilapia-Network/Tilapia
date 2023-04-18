package net.tilapiamc.proxyapi.game

import net.tilapiamc.proxyapi.servers.TilapiaServer
import java.util.*

abstract class MiniGame(
    server: TilapiaServer, gameId: UUID, managed: Boolean, val lobbyType: String, val miniGameType: String
): Game(server, GameType.MINIGAME, gameId, managed) {

    override fun equals(other: Any?): Boolean {
        return other is MiniGame && other.gameId == gameId
    }


}