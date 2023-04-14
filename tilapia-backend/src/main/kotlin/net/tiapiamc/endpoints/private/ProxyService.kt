package net.tiapiamc.endpoints.private

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import net.tiapiamc.session.ProxySession
import net.tiapiamc.session.SessionManager
import java.util.*

object ProxyService {

    fun Application.applyProxyService() {
        routing {
            authenticate("private-api") {
                get("/proxy/list") {

                }
                get("/proxy/info") {

                }
                webSocket("/proxy/gateway") {
                    val proxyId = UUID.randomUUID()
                    val session = ProxySession(call.request.origin.remoteHost, this, proxyId)
                    session.onSessionStarted.add {
                        SessionManager.proxies[proxyId] = session
                    }
                    session.onSessionClosed.add {
                        SessionManager.proxies.remove(proxyId)
                    }
                    session.startSession()
                    // TODO: Fix proxy ID
                }
            }
        }
    }

}