package net.tilapiamc.obj.game

import net.tilapiamc.obj.Player
import net.tilapiamc.session.ServerSession
import net.tilapiamc.communication.GameData
import net.tilapiamc.communication.GameType
import java.util.*

abstract class Game(val server: ServerSession, val gameId: UUID, val players: MutableList<Player> = ArrayList<Player>()) {

    fun toInfo(): GameData {
        if (this is Lobby) {
            return GameData(null, toLobbyInfo())
        } else if (this is MiniGame) {
            return GameData(toMiniGameInfo(), null)
        }
        error("Unsupported game type")
    }

    abstract fun getGameType(): GameType

}