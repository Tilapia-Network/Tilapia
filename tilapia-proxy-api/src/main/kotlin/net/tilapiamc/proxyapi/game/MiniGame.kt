package net.tilapiamc.proxyapi.game

import com.google.gson.JsonObject
import net.tilapiamc.proxyapi.player.NetworkPlayer
import net.tilapiamc.proxyapi.servers.TilapiaServer
import java.util.*
import kotlin.collections.ArrayList

abstract class MiniGame(
    server: TilapiaServer,
    gameId: UUID,
    managed: Boolean,
    val lobbyType: String,
    val miniGameType: String,
    properties: JsonObject
): Game(server, GameType.MINIGAME, gameId, managed, properties) {
    val spectators: List<NetworkPlayer> = ArrayList()

    override fun equals(other: Any?): Boolean {
        return other is MiniGame && other.gameId == gameId
    }


}