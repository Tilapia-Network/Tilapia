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
            setBody(gameInfo)
        }
        ensureResponse(response)
    }
    suspend fun endGame(gameInfo: GameInfo) {
        val response = client.post("/game/end") {
            setBody(gameInfo)
        }
        ensureResponse(response)
    }



    // Game Service
    fun getTypes(gameType: GameType): List<String> {
        TODO()
    }
    fun getGamesForPlayer(lobbyType: String? = null, miniGameType: String? = null, gameIdPrefix: String? = null): HashMap<GameData, JoinResult> {
        TODO()

    }
    fun getGames(lobbyType: String? = null, miniGameType: String? = null, gameIdPrefix: String? = null): List<GameData> {
        TODO()
    }


    fun where(player: UUID): GameData {
        TODO()

    }
    fun send(player: UUID, gameId: UUID): JoinResult {
        TODO()
    }


    suspend fun ensureResponse(response: HttpResponse): String {
        if (response.status != HttpStatusCode.OK) {
            throw IllegalStateException("Server returned status: ${response.status}, body:  ${response.bodyAsText()}")
        }
        return response.bodyAsText()
    }
}