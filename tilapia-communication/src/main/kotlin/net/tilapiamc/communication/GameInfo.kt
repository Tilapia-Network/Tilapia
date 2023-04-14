package net.tilapiamc.communication

import java.util.*

open class GameInfo(
    val serverId: UUID,
    val gameId: UUID,
    val lobbyType: String,
) {



}

class MiniGameInfo(serverId: UUID,
                   gameId: UUID,
                   lobbyType: String,
                   val miniGameType: String
) : GameInfo(serverId, gameId, lobbyType) {
}

class LobbyInfo(serverId: UUID,
                gameId: UUID,
                lobbyType: String,
) : GameInfo(serverId, gameId, lobbyType)