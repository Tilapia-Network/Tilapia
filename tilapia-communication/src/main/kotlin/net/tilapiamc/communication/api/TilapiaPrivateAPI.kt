package net.tilapiamc.communication.api

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import net.tilapiamc.communication.*
import java.util.*

open class TilapiaPrivateAPI(val client: HttpClient) {

    val gson = GsonBuilder().create()

    // ServerService
    suspend fun listServers(serverIdPrefix: String? = null, proxyIdPrefix: String? = null): List<ServerInfo> {
        val response = client.get("/server/list") {
            if (serverIdPrefix != null) {
                parameter("serverIdPrefix", serverIdPrefix)
            }
            if (proxyIdPrefix != null) {
                parameter("proxyIdPrefix", proxyIdPrefix)
            }
        }
        val array = gson.fromJson(ensureResponse(response), JsonArray::class.java)
        return array.map { gson.fromJson(it, ServerInfo::class.java) }
    }

    // Proxy Service
    suspend fun listProxies(proxyIdPrefix: String? = null): List<ProxyInfo> {
        val response = client.get("/proxy/list") {
            if (proxyIdPrefix != null) {
                parameter("proxyIdPrefix", proxyIdPrefix)
            }
        }
        val array = gson.fromJson(ensureResponse(response), JsonArray::class.java)
        return array.map { gson.fromJson(it, ProxyInfo::class.java) }
    }

    // Game Service
    suspend fun registerGame(gameInfo: GameInfo) {
        val response = client.post("/game/register") {
            contentType(ContentType.Application.Json)
            if (gameInfo is LobbyInfo) {
                setBody(GameData(null, gameInfo))
            }
            if (gameInfo is MiniGameInfo) {
                setBody(GameData(gameInfo, null))
            }
        }
        ensureResponse(response)
    }
    suspend fun endGame(gameInfo: GameInfo) {
        val response = client.post("/game/end") {
            contentType(ContentType.Application.Json)
            if (gameInfo is LobbyInfo) {
                setBody(GameData(null, gameInfo))
            }
            if (gameInfo is MiniGameInfo) {
                setBody(GameData(gameInfo, null))
            }
        }
        ensureResponse(response)
    }



    // Game Service
    suspend fun getTypes(gameType: GameType? = null): List<String> {
        val response = client.get("/game/types") {
            if (gameType != null) {
                parameter("gameType", gameType.name)
            }
        }
        val responseBody = gson.fromJson(ensureResponse(response), JsonArray::class.java)
        return responseBody.map { it.asString }
    }
    suspend fun getGamesForPlayer(player: UUID, lobbyType: String? = null, miniGameType: String? = null, gameIdPrefix: String? = null, forceJoin: Boolean = false): HashMap<GameData, JoinResult> {
        val response = client.get("/game/for-player") {
            if (lobbyType != null) {
                parameter("lobbyType", lobbyType)
            }
            if (miniGameType != null) {
                parameter("miniGameType", miniGameType)
            }
            if (gameIdPrefix != null) {
                parameter("gameIdPrefix", gameIdPrefix)
            }
            parameter("player", player.toString())
            parameter("forceJoin", forceJoin)
        }
        val responseBody = gson.fromJson(ensureResponse(response), JsonArray::class.java)
        val out = HashMap<GameData, JoinResult>()
        for (jsonElement in responseBody) {
            out[gson.fromJson(jsonElement.asJsonArray[0], GameData::class.java)] = gson.fromJson(jsonElement.asJsonArray[1], JoinResult::class.java)
        }
        return out

    }
    suspend fun getGames(lobbyType: String? = null, miniGameType: String? = null, gameIdPrefix: String? = null): List<GameData> {
        val response = client.get("/game/list") {
            if (lobbyType != null) {
                parameter("lobbyType", lobbyType)
            }
            if (miniGameType != null) {
                parameter("miniGameType", miniGameType)
            }
            if (gameIdPrefix != null) {
                parameter("gameIdPrefix", gameIdPrefix)
            }
        }
        val responseBody = gson.fromJson(ensureResponse(response), JsonArray::class.java)
        return responseBody.map { gson.fromJson(it, GameData::class.java) }
    }


    suspend fun where(player: UUID): GameData {
        val response = client.get("/player/where") {
            parameter("player", player)
        }
        return gson.fromJson(ensureResponse(response), GameData::class.java)

    }
    suspend fun send(player: UUID, gameId: UUID, forceJoin: Boolean = false): JoinResult {
        val response = client.post("/player/send") {
            parameter("player", player)
            parameter("gameId", gameId)
            parameter("forceJoin", forceJoin)
        }
        if (response.status != HttpStatusCode.OK && response.status != HttpStatusCode.NotAcceptable) {
            throw IllegalStateException("Server returned status: ${response.status}, body:  ${response.bodyAsText()}")
        }
        return gson.fromJson(response.bodyAsText(), JoinResult::class.java)
    }


    suspend fun ensureResponse(response: HttpResponse): String {
        if (response.status != HttpStatusCode.OK) {
            throw IllegalStateException("Server returned status: ${response.status}, body:  ${response.bodyAsText()}")
        }
        return response.bodyAsText()
    }
}