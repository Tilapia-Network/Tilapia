package net.tilapiamc.endpoints.private

import com.google.gson.Gson
import com.google.gson.JsonArray
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.runBlocking
import net.tilapiamc.managers.ServerManager
import net.tilapiamc.obj.game.Game
import net.tilapiamc.obj.game.Lobby
import net.tilapiamc.obj.game.MiniGame
import net.tilapiamc.common.json.jsonArrayOf
import net.tilapiamc.communication.*
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.Future

object GameService {
    fun ApplicationCall.filterGames(serverManager: ServerManager): List<Game> {
        val gameIdPrefix = parameters["gameIdPrefix"]
        val lobbyType = parameters["lobbyType"]
        val miniGameType = parameters["miniGameType"]
        return serverManager.games.values.filter {
            if (gameIdPrefix != null)
                it.gameId.toString().lowercase().startsWith(gameIdPrefix.lowercase())
            else
                true
        }.filter {
            if (lobbyType != null)
                if (it is Lobby)
                    lobbyType == it.lobbyType
                else
                    false
            else
                true
        }.filter {
            if (miniGameType != null)
                if (it is MiniGame)
                    miniGameType == it.miniGameType
                else
                    false
            else
                true
        }
    }
    fun Application.applyGameService(serverManager: ServerManager, gson: Gson) {
        routing {
            authenticate("private-api") {
                post<GameData>("/game/register") {
                    if ((it.miniGame != null && it.lobby != null) || (it.miniGame == null && it.lobby == null)) {
                        call.respond(HttpStatusCode.BadRequest, "Only one game registration in 1 request is support")
                        return@post
                    }
                    val game: GameInfo = if (it.miniGame != null) {
                        it.miniGame!!
                    } else {
                        it.lobby!!

                    }
                    if (game.players.isNotEmpty()) {
                        call.respond(HttpStatusCode.BadRequest, "A game registration could not contain any player")
                        return@post
                    }
                    if (game.gameId in serverManager.games) {
                        call.respond(HttpStatusCode.Conflict, "A game with ID: ${game.gameId} already exists")
                        return@post
                    }
                    val server = serverManager.servers[game.serverId]
                    if (server == null) {
                        call.respond(HttpStatusCode.NotFound, "The server with ID ${game.serverId} could not be found")
                        return@post
                    }
                    serverManager.logger.info("Registered game: ${game.gameId}")
                    if (game is LobbyInfo) {
                        val lobby = Lobby(server, game.gameId, game.lobbyType)
                        server.games.add(lobby)
                        serverManager.games[lobby.gameId] = lobby
                    } else if (game is MiniGameInfo) {
                        val miniGame = MiniGame(server, game.gameId, game.lobbyType, game.miniGameType)
                        server.games.add(miniGame)
                        serverManager.games[miniGame.gameId] = miniGame
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "Unsupported game type")
                        return@post
                    }
                    call.respond(HttpStatusCode.OK)
                }
                post("/game/end") {
                    val gameId = call.parameters["gameId"]?.let { UUID.fromString(it) }
                    if (gameId in serverManager.games) {
                        val game = serverManager.games[gameId]!!
                        if (game.players.isNotEmpty()) {
                            call.respond(HttpStatusCode.BadRequest, "The game still has players in it")
                            return@post
                        }
                        serverManager.logger.info("Unregistered game: ${game.gameId}")
                        serverManager.games.remove(game.gameId)
                        val server = serverManager.servers[game.server.serverId]
                        if (server == null) {
                            call.respond(HttpStatusCode.OK)
                            return@post
                        }
                        server.games.removeIf { it.gameId == game.gameId }
                        call.respond(HttpStatusCode.OK)
                    } else {
                        call.respond(HttpStatusCode.NotFound, "The requested game is not found")
                    }

                }
                get("/game/list") {
                    call.respond(call.filterGames(serverManager).map { it.toInfo() })
                }
                get("/game/for-player") {
                    val out = JsonArray()
                    val forceJoin = call.parameters["forceJoin"]?.toBoolean()?:false
                    val games = call.filterGames(serverManager)
                    val player = serverManager.players[UUID.fromString(call.parameters["player"])]
                    if (player == null) {
                        call.respond(HttpStatusCode.NotFound, "Player is not found")
                        return@get
                    }
                    val threadPool = Executors.newFixedThreadPool(100)
                    val results = ArrayList<Future<*>>()
                    for (game in games) {
                        results.add(threadPool.submit {
                            runBlocking {
                                out.add(gson.jsonArrayOf(game.toInfo(), game.server.getJoinResult(game.gameId, player.toPlayerInfo(), forceJoin)))
                            }
                        })
                    }
                    results.forEach { it.get() }
                    call.respond(out)
                }
                get("/game/types") {
                    val gameType = call.parameters["gameType"]?.let { GameType.valueOf(it) }

                    call.respond(serverManager.games.values
                        .filter {
                            if (gameType != null) it.getGameType() == gameType else true
                        }
                        .map { if (it is Lobby) it.lobbyType else if (it is MiniGame) it.miniGameType else "" }
                        .toSet())
                }
            }
        }
    }



}