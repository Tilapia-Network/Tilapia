package net.tiapiamc.obj.game

import net.tiapiamc.obj.Server
import net.tilapiamc.communication.LobbyInfo
import java.util.*

class Lobby(server: Server, gameId: UUID, val lobbyType: String) : Game(server, gameId) {

    fun toLobbyInfo(): LobbyInfo {
        return LobbyInfo(server.serverId, gameId, lobbyType)
    }

}