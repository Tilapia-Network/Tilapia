package net.tiapiamc.endpoints.private

import com.google.gson.Gson
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import net.tiapiamc.managers.ServerManager
import net.tiapiamc.session.ProxySession
import net.tilapiamc.communication.ProxyInfo

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
                    session.onSessionStarted.add {
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