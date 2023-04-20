package net.tilapiamc.obj.game

import com.google.gson.JsonObject
import net.tilapiamc.communication.GameType
import net.tilapiamc.communication.LobbyInfo
import net.tilapiamc.obj.Player
import net.tilapiamc.session.ServerSession
import java.util.*

class Lobby(server: ServerSession,
            gameId: UUID,
            val lobbyType: String,
            players: MutableList<Player> = ArrayList<Player>(),
            properties: JsonObject = JsonObject()) : Game(server, gameId, players, properties) {

    fun toLobbyInfo(): LobbyInfo {
        return LobbyInfo(server.serverId, gameId, lobbyType, players.map { it.toPlayerInfo() }, properties)
    }

    override fun getGameType(): GameType {
        return GameType.LOBBY
    }

}