package net.tilapiamc.obj.game

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import net.tilapiamc.communication.GameType
import net.tilapiamc.communication.MiniGameInfo
import net.tilapiamc.obj.Player
import net.tilapiamc.session.ServerSession
import java.util.*
import kotlin.collections.HashMap

class MiniGame(server: ServerSession,
               gameId: UUID,
               val lobbyType: String,
               val miniGameType: String,
               players: MutableList<Player> = ArrayList<Player>(),
               properties: JsonObject = JsonObject()
) : Game(server, gameId, players, properties) {

    fun toMiniGameInfo(): MiniGameInfo {
        return MiniGameInfo(
            server.serverId,
            gameId,
            lobbyType,
            players.map { it.toPlayerInfo() },
            miniGameType,
            properties,
        )
    }

    override fun getGameType(): GameType {
        return GameType.MINIGAME
    }

}