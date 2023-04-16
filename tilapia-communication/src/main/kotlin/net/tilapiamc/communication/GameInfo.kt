package net.tilapiamc.communication

import java.util.*

abstract class GameInfo(
    val serverId: UUID,
    val gameId: UUID,
    val lobbyType: String,
    val players: List<PlayerInfo>,
) {



}

class MiniGameInfo(serverId: UUID,
                   gameId: UUID,
                   lobbyType: String,
                   players: List<PlayerInfo>,
                   val miniGameType: String
) : GameInfo(serverId, gameId, lobbyType, players) {
}

class LobbyInfo(serverId: UUID,
                gameId: UUID,
                lobbyType: String,
                players: List<PlayerInfo>,
) : GameInfo(serverId, gameId, lobbyType, players)