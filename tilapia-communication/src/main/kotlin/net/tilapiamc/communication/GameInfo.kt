package net.tilapiamc.communication

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.util.*

abstract class GameInfo(
    val serverId: UUID,
    val gameId: UUID,
    val lobbyType: String,
    val players: List<PlayerInfo>,
    val properties: JsonObject = JsonObject()
) {



}

class MiniGameInfo(serverId: UUID,
                   gameId: UUID,
                   lobbyType: String,
                   players: List<PlayerInfo>,
                   val miniGameType: String,
                   properties: JsonObject = JsonObject()
) : GameInfo(serverId, gameId, lobbyType, players, properties) {
}

class LobbyInfo(serverId: UUID,
                gameId: UUID,
                lobbyType: String,
                players: List<PlayerInfo>,
                properties: JsonObject = JsonObject()
) : GameInfo(serverId, gameId, lobbyType, players, properties)