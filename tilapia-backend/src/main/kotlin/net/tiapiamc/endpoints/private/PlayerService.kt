package net.tiapiamc.endpoints.private

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

object PlayerService {

    fun Application.applyPlayerService() {
        routing {
            authenticate("private-api") {
                get("/player/where") {

                }
                post("/player/send") {

                }
            }

        }
    }

}