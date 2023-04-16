package net.tiapiamc.endpoints.private

import com.google.gson.Gson
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.tiapiamc.managers.ServerManager
import net.tiapiamc.obj.game.Lobby
import net.tiapiamc.obj.game.MiniGame
import net.tilapiamc.communication.GameData
import net.tilapiamc.communication.GameInfo
import net.tilapiamc.communication.LobbyInfo
import net.tilapiamc.communication.MiniGameInfo

object GameService {

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
                post<GameData>("/game/end") {
                    if ((it.miniGame != null && it.lobby != null) || (it.miniGame == null && it.lobby == null)) {
                        call.respond(HttpStatusCode.BadRequest, "Only one game registration in 1 request is support")
                        return@post
                    }
                    val game: GameInfo = if (it.miniGame != null) {
                        it.miniGame!!
                    } else {
                        it.lobby!!
                    }
                    if (game.gameId in serverManager.games) {
                        val game = serverManager.games[game.gameId]!!
                        if (game.players.isNotEmpty()) {
                            call.respond(HttpStatusCode.BadRequest, "The game still has players in it")
                            return@post
                        }
                        serverManager.games.remove(game.gameId)
                    } else {
                        call.respond(HttpStatusCode.NotFound, "The requested game is not found")
                    }
                    val server = serverManager.servers[game.serverId]
                    if (server == null) {
                        call.respond(HttpStatusCode.OK)
                        return@post
                    }
                    server.games.removeIf { it.gameId == game.gameId }

                    call.respond(HttpStatusCode.OK)
                }
                get("/game/list") {
                    // game type
                    // game ID starts with
                    // type
                }
                get("/game/for-player") {
                    // game ID starts with
                    // type
                }
                get("/game/types") {
                    // Game type
                }
            }
        }
    }

}