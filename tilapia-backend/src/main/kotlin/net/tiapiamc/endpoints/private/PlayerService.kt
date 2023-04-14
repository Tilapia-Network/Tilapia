package net.tiapiamc.endpoints.private

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*

object PlayerService {

    fun Application.applyPlayerService() {
        routing {
            post("/player/login") {
                // Requires Proxy Session ID

            }
            delete("/player/logout") {
                // Requires Proxy Session ID

            }

            get("/player/where") {

            }
            post("/player/send") {

            }
        }
    }

}