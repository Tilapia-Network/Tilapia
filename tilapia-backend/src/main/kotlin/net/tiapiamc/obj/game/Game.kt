package net.tiapiamc.obj.game

import net.tiapiamc.obj.Server
import net.tilapiamc.communication.GameData
import net.tilapiamc.communication.GameInfo
import java.util.UUID

abstract class Game(val server: Server, val gameId: UUID) {

    fun toInfo(): GameData {
        if (this is Lobby) {
            return GameData(null, toLobbyInfo())
        } else if (this is MiniGame) {
            return GameData(toMiniGameInfo(), null)
        }
        error("Unsupported game type")
    }

}