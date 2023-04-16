package net.tiapiamc.endpoints.private

import com.google.gson.Gson
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import net.tiapiamc.managers.ServerManager
import net.tiapiamc.obj.Player.Companion.toPlayer
import net.tiapiamc.session.ProxySession
import net.tilapiamc.communication.ProxyInfo
import net.tilapiamc.communication.session.client.proxy.CPacketProxyPlayerLogin
import net.tilapiamc.communication.session.client.proxy.CPacketProxyPlayerLogout

object ProxyService {

    fun Application.applyProxyService(serverManager: ServerManager, gson: Gson) {
        routing {
            authenticate("private-api") {
                get("/proxy/list") {
                    val proxyIdPrefix = call.parameters["proxyIdPrefix"]
                    val out = ArrayList<ProxyInfo>()

                    out.addAll(serverManager.proxies.values.filter {
                        if (proxyIdPrefix != null) {
                            it.proxyId.toString().lowercase().startsWith(proxyIdPrefix.lowercase())
                        } else true
                    }.map { it.toProxyInfo() })

                    call.respond(out)
                }
                webSocket("/proxy/gateway") {
                    val proxyId = serverManager.generateProxyId()
                    val session = ProxySession(call.request.origin.remoteHost, this, proxyId)

                    session.onPacket.add {
                        val packet = it.packet
                        if (packet is CPacketProxyPlayerLogin) {
                            val player = packet.playerInfo.toPlayer(serverManager)
                            serverManager.logger.info("Player ${player.playerName} (${player.uuid}) has logged in")
                            session.players[packet.playerInfo.uniqueId] = player
                            serverManager.players[packet.playerInfo.uniqueId]  = player
                        }
                        if (packet is CPacketProxyPlayerLogout) {
                            val player = session.players[packet.playerUUID]
                            if (player != null) {
                                serverManager.logger.info("Player ${player.playerName} (${player.uuid}) has logged out")
                                session.players.remove(player.uuid)
                                serverManager.players.remove(player.uuid)
                                serverManager.games.values.filter { player in it.players }.forEach { game ->
                                    game.players.remove(player)
                                }
                            }
                        }
                    }
                    session.onHandshakeFinished.add {
                        serverManager.createProxy(session)
                    }
                    session.onSessionClosed.add {
                        serverManager.deleteProxy(session, it.closedBySelf, it.closeReason)
                    }
                    session.startSession()
                }
            }
        }
    }

}