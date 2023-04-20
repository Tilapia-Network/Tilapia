package net.tilapiamc.proxyapi.game

import com.google.gson.JsonObject
import net.tilapiamc.proxyapi.player.NetworkPlayer
import net.tilapiamc.proxyapi.servers.TilapiaServer
import java.util.*

interface IGame {

    val server: TilapiaServer
    val gameType: GameType
    val gameId: UUID
    val managed: Boolean
    val players: ArrayList<NetworkPlayer>
    val properties: JsonObject

    val shortGameId: String
        get() = gameId.toString().split("-")[0]
}