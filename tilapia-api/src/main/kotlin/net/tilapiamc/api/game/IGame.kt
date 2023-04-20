package net.tilapiamc.api.game

import com.google.gson.JsonElement
import net.tilapiamc.api.player.NetworkPlayer
import net.tilapiamc.api.server.TilapiaServer
import java.util.*

interface IGame {

    val server: TilapiaServer
    val gameType: GameType
    val gameId: UUID
    val managed: Boolean
    val players: ArrayList<NetworkPlayer>

    val shortGameId: String
        get() = gameId.toString().split("-")[0]

    fun hasProperty(name: String): Boolean {
        return getProperty(name) != null
    }
    fun getProperty(name: String): JsonElement?
    fun removeProperty(name: String)
    fun setProperty(name: String, value: Any)
    fun getProperties(): Map<String, JsonElement>
}