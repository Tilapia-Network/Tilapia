package net.tiapiamc.endpoints.private

import com.google.gson.Gson
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.tiapiamc.managers.ServerManager
import net.tilapiamc.communication.GameData
import net.tilapiamc.communication.GameType
import net.tilapiamc.communication.session.client.CPacketAcknowledge
import net.tilapiamc.communication.session.server.proxy.SPacketProxyAcceptPlayer
import net.tilapiamc.communication.session.server.server.SPacketServerAcceptPlayer
import java.util.*

object PlayerService {

    fun Application.applyPlayerService(serverManager: ServerManager, gson: Gson) {
        routing {
            authenticate("private-api") {
                get("/player/where") {
                    val player = call.parameters["player"]
                    if (player == null || serverManager.players[UUID.fromString(player)] == null) {
                        call.respond(HttpStatusCode.BadRequest, "Player is not found")
                        return@get
                    }
                    val playerInstance = serverManager.players[UUID.fromString(player)]!!
                    call.respond(playerInstance.currentGame?.toInfo()?:GameData(null, null))
                }
                post("/player/send") {
                    val player = call.parameters["player"]
                    val gameId = call.parameters["gameId"]
                    val forceJoin = call.parameters["forceJoin"]?.toBooleanStrictOrNull()?:false
                    var spectate = call.parameters["spectate"]?.toBooleanStrictOrNull()?:false
                    if (player?.let { serverManager.players[UUID.fromString(it)] } == null) {
                        call.respond(HttpStatusCode.BadRequest, "Player is not found")
                        return@post
                    }
                    if (gameId?.let { serverManager.games[UUID.fromString(it)] } == null) {
                        call.respond(HttpStatusCode.BadRequest, "Game is not found")
                        return@post
                    }
                    val playerInstance = serverManager.players[UUID.fromString(player)]!!
                    val gameInstance = serverManager.games[UUID.fromString(gameId)]!!
                    try {
                        playerInstance.joiningLock.lock()
                        val server = gameInstance.server
                        val proxy = gameInstance.server.proxy
                        if (gameInstance.getGameType() != GameType.MINIGAME) {
                            spectate = false
                        }
                        val joinResult = try {
                            server.getJoinResult(
                                gameInstance.gameId,
                                playerInstance.toPlayerInfo(),
                                forceJoin
                            )
                        } catch (e: IllegalStateException) {
                            // TODO: Bad practice on hard-coded string, should use custom exception
                            if (e.message == "Server did not respond") {
                                call.respond(HttpStatusCode.ServiceUnavailable, "The server did not respond a join result")
                                return@post
                            }
                            throw e
                        }

                        if (!joinResult.success) {
                            call.respond(HttpStatusCode.NotAcceptable, joinResult)
                            return@post
                        }
                        val transmissionId = server.newTransmissionId()

                        server.waitForPacketWithType<CPacketAcknowledge>({ it.transmissionId == transmissionId }) {
                            server.sendPacket(SPacketServerAcceptPlayer(transmissionId, gameInstance.server.serverId, gameInstance.gameId, playerInstance.uuid, spectate))
                        }?:run {
                            call.respond(HttpStatusCode.ServiceUnavailable, "The server did not respond to accept player packet")
                            return@post
                        }

                        proxy.waitForPacketWithType<CPacketAcknowledge>({ it.transmissionId == transmissionId }) {
                            proxy.sendPacket(SPacketProxyAcceptPlayer(transmissionId, server.serverId, playerInstance.uuid))
                        }?:run {
                            call.respond(HttpStatusCode.ServiceUnavailable, "The proxy did not respond to accept player packet")
                            return@post
                        }
                        gameInstance.players.add(playerInstance)
                        playerInstance.currentGame = gameInstance

                        call.respond(HttpStatusCode.OK, joinResult)
                    } finally {
                        playerInstance.joiningLock.unlock()
                    }

                }
            }

        }
    }

}