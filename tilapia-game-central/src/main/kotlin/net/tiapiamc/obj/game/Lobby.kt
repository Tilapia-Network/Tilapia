package net.tiapiamc.obj.game

import net.tiapiamc.obj.Player
import net.tiapiamc.session.ServerSession
import net.tilapiamc.communication.GameType
import net.tilapiamc.communication.LobbyInfo
import java.util.*

class Lobby(server: ServerSession, gameId: UUID, val lobbyType: String, players: MutableList<Player> = ArrayList<Player>()) : Game(server, gameId, players) {

    fun toLobbyInfo(): LobbyInfo {
        return LobbyInfo(server.serverId, gameId, lobbyType, players.map { it.toPlayerInfo() })
    }

    override fun getGameType(): GameType {
        return GameType.LOBBY
    }

}