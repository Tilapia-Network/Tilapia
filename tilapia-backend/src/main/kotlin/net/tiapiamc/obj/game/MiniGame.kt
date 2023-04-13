package net.tiapiamc.obj.game

import net.tiapiamc.obj.Server
import net.tilapiamc.communication.MiniGameInfo
import java.util.*

class MiniGame(server: Server, gameId: UUID, val lobbyType: String, val miniGameType: String) : Game(server, gameId) {

    fun toMiniGameInfo(): MiniGameInfo {
        return MiniGameInfo(
            server.serverId,
            gameId,
            lobbyType,
            miniGameType
        )
    }

}