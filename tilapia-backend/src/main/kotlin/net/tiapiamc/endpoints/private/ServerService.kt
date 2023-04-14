package net.tiapiamc.endpoints.private

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*

object ServerService {

    fun Application.applyServerService() {
        routing {
            get("/server/list") {

            }
            get("/server/info") {

            }
            webSocket("/server/gateway") {

            }
        }
    }

}