package net.tiapiamc.endpoints.private

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import net.tiapiamc.session.ServerSession
import net.tiapiamc.session.SessionManager
import java.util.*

object ServerService {

    fun Application.applyServerService() {
        routing {
            authenticate("private-api") {
                get("/server/list") {

                }
                get("/server/info") {

                }
                webSocket("/server/gateway") {
                    val serverId = UUID.randomUUID()
                    // TODO: Fix proxy ID
                    val session = ServerSession(call.request.origin.remoteHost, this, UUID.randomUUID(), serverId)
                    session.onSessionStarted.add {
                        SessionManager.servers[serverId] = session
                    }
                    session.onSessionClosed.add {
                        SessionManager.servers.remove(serverId)
                    }
                    session.startSession()
                }
            }
        }
    }

}