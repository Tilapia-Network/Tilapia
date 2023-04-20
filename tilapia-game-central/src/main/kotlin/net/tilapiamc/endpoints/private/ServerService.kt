package net.tilapiamc.endpoints.private

import com.google.gson.Gson
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import net.tilapiamc.communication.ServerInfo
import net.tilapiamc.managers.ServerManager
import net.tilapiamc.session.ServerSession

object ServerService {

    fun Application.applyServerService(serverManager: ServerManager, gson: Gson) {
        routing {
            authenticate("private-api") {
                get("/server/list") {
                    val serverIdPrefix = call.parameters["serverIdPrefix"]
                    val proxyIdPrefix = call.parameters["proxyIdPrefix"]

                    val out = ArrayList<ServerInfo>()

                    out.addAll(serverManager.servers.values.filter {
                        if (proxyIdPrefix != null) {
                            it.proxy.proxyId.toString().lowercase().startsWith(proxyIdPrefix.lowercase())
                        } else true
                    }.filter {
                        if (serverIdPrefix != null) {
                            it.serverId.toString().lowercase().startsWith(serverIdPrefix.lowercase())
                        } else true
                    }.map { it.toServerInfo() })

                    call.respond(out)
                }
            }

            webSocket("/server/gateway") {
                if (serverManager.proxies.isEmpty()) {
                    serverManager.logger.warn("A server has attempted to connect to gateway while there's no proxy connected")
                    close(CloseReason(CloseReason.Codes.NORMAL, "No proxy is connected"))
                    return@webSocket
                }
                val port = call.parameters["port"]!!.toInt()
                val serverId = serverManager.generateServerId()
                val proxyId = serverManager.getProxyAssignment()
                val session = ServerSession(call.request.local.remoteAddress, port, this, proxyId, serverId)
                session.onHandshakeFinished.add {
                    serverManager.createServer(session)
                }
                session.onSessionClosed.add {
                    serverManager.deleteServer(session, it.closedBySelf, it.closeReason)
                }
                flush()
                session.startSession()
            }
        }
    }

}