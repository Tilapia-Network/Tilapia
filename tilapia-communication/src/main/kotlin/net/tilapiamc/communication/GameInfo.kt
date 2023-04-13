package net.tilapiamc.communication

import java.util.UUID

open class GameInfo(
    val gameId: UUID,
    val lobbyType: String,
) {



}

class MiniGameInfo(gameId: UUID,
                   lobbyType: String,
                   val gameType: String
) : GameInfo(gameId, lobbyType) {
}

class LobbyInfo(gameId: UUID,
                lobbyType: String,
) : GameInfo(gameId, lobbyType)