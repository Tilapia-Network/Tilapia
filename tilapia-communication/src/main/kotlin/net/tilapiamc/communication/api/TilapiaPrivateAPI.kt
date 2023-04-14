package net.tilapiamc.communication.api

import com.google.gson.JsonPrimitive
import io.ktor.client.*
import net.tilapiamc.communication.*
import java.util.*

open class TilapiaPrivateAPI(val client: HttpClient) {

    // ServerService
    fun listServers(): List<ServerInfo> {
        TODO()

    }
    fun getServerInfo(serverId: UUID): ServerInfo? {
        TODO()

    }

    // Proxy Service
    fun getProxyInfo(proxyId: UUID): ProxyInfo? {
        TODO()

    }

    fun listProxies(): List<ProxyInfo> {
        TODO()

    }


    // Game Service
    fun getLobbyTypes(): List<String> {
        TODO()

    }
    fun getMiniGameTypes(): List<String> {
        TODO()

    }
    fun getLobbiesForPlayer(uuid: UUID): HashMap<LobbyInfo, JoinResult> {
        TODO()

    }
    fun getMiniGamesForPlayer(uuid: UUID): HashMap<MiniGameInfo, JoinResult> {
        TODO()

    }
    fun getLobbies(): List<LobbyInfo> {
        TODO()

    }
    fun getMiniGames(): List<MiniGameInfo> {
        TODO()

    }
    fun findLobbies(lobbyType: String): List<LobbyInfo> {
        TODO()

    }
    fun findMiniGames(miniGameType: String): List<MiniGameInfo> {
        TODO()

    }
    fun getFromId(uuid: UUID): GameData {
        TODO()

    }
    fun getFromShortId(shortId: String): GameData {
        TODO()

    }


    fun where(player: UUID): GameData {
        TODO()

    }
    fun send(player: UUID, gameId: UUID): JoinResult {
        TODO()

    }


    // Data Service
    fun getPlayerData(offlinePlayer: UUID, tableName: String): Map<String, JsonPrimitive> {
        TODO()

    }
    fun setPlayerData(offlinePlayer: UUID, tableName: String, data: Map<String, JsonPrimitive>) {
        TODO()

    }
}