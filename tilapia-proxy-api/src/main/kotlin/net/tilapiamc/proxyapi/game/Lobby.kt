package net.tilapiamc.proxyapi.game

import com.google.gson.JsonObject
import net.tilapiamc.proxyapi.servers.TilapiaServer
import java.util.*

abstract class Lobby(
    server: TilapiaServer, gameId: UUID, managed: Boolean, val lobbyType: String, properties: JsonObject
): Game(server, GameType.LOBBY, gameId, managed, properties) {

    override fun equals(other: Any?): Boolean {
        return other is Lobby && other.gameId == gameId
    }

}