package net.tilapiamc.obj.game

import net.tilapiamc.obj.Player
import net.tilapiamc.session.ServerSession
import net.tilapiamc.communication.GameType
import net.tilapiamc.communication.MiniGameInfo
import java.util.*

class MiniGame(server: ServerSession, gameId: UUID, val lobbyType: String, val miniGameType: String, players: MutableList<Player> = ArrayList<Player>()) : Game(server, gameId, players) {

    fun toMiniGameInfo(): MiniGameInfo {
        return MiniGameInfo(
            server.serverId,
            gameId,
            lobbyType,
            players.map { it.toPlayerInfo() },
            miniGameType,
        )
    }

    override fun getGameType(): GameType {
        return GameType.MINIGAME
    }

}