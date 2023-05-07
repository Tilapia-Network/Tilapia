package net.tilapiamc.communication.api

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.gson.*
import net.tilapiamc.communication.*
import java.util.*
import java.util.concurrent.Executors

open class TilapiaPrivateAPI(val client: HttpClient) {

    companion object {
        fun getHttpClient(apiKey: String, baseURL: String): HttpClient = HttpClient {
            install(WebSockets) {
                maxFrameSize = Long.MAX_VALUE
                pingInterval = 15000
            }
            install(ContentNegotiation) {
                gson()
            }
            install(Auth) {
                bearer {
                    loadTokens {
                        BearerTokens(apiKey, "")
                    }
                }
            }
            defaultRequest {
                url(baseURL)
            }
        }
    }

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
        if (gameInfo is LobbyInfo) {
            registerGame(GameData(null, gameInfo))
        }
        if (gameInfo is MiniGameInfo) {
            registerGame(GameData(gameInfo, null))
        }
    }
    // Game Service
    suspend fun updateGameProperty(gameId: UUID, properties: Map<String, JsonElement>) {
        val response = client.post("/game/update-properties") {
            parameter("gameId", gameId)
            contentType(ContentType.Application.Json)
            setBody(properties)
        }
        ensureResponse(response)
    }
    suspend fun registerGame(gameData: GameData) {
        val response = client.post("/game/register") {
            contentType(ContentType.Application.Json)
            setBody(gameData)
        }
        ensureResponse(response)
    }
    suspend fun endGame(gameId: UUID) {
        val response = client.post("/game/end") {
            parameter("gameId", gameId)
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



    suspend fun send(player: UUID, gameId: UUID, forceJoin: Boolean = false, spectate: Boolean = false): JoinResult {
        // TODO: Player joining too fast
        // See #5
        val response = client.post("/player/send") {
            parameter("player", player)
            parameter("gameId", gameId)
            parameter("forceJoin", forceJoin)
            parameter("spectate", spectate)
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