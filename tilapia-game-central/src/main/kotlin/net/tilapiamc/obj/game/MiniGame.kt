package net.tilapiamc.obj.game

import com.google.gson.JsonObject
import net.tilapiamc.communication.GameType
import net.tilapiamc.communication.MiniGameInfo
import net.tilapiamc.obj.Player
import net.tilapiamc.session.ServerSession
import java.util.*

class MiniGame(server: ServerSession,
               gameId: UUID,
               val lobbyType: String,
               val miniGameType: String,
               players: MutableList<Player> = ArrayList<Player>(),
               val spectators: MutableList<Player> = ArrayList<Player>(),
               properties: JsonObject = JsonObject()
) : Game(server, gameId, players, properties) {

    fun toMiniGameInfo(): MiniGameInfo {
        return MiniGameInfo(
            server.serverId,
            gameId,
            lobbyType,
            players.map { it.toPlayerInfo() },
            miniGameType,
            spectators.map { it.toPlayerInfo() },
            properties,
        )
    }

    override fun getGameType(): GameType {
        return GameType.MINIGAME
    }

}