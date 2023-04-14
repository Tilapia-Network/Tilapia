package net.tiapiamc.endpoints.private

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*

object GameService {

    fun Application.applyGameService() {
        routing {
            post("/game/register") {
                // Requires Session ID
            }
            get("/game/list") {

            }
            get("/game/info") {

            }
            get("/game/info-from-short-id") {

            }
            get("/game/minigame/list") {

            }
            get("/game/lobby/list") {

            }
            get("/game/minigame/types") {

            }
            get("/game/lobby/types") {

            }
            get("/game/minigame/list-from-type") {

            }
            get("/game/lobby/list-from-type") {

            }
            get("/game/minigame/for-player") {

            }
            get("/game/lobby/for-player") {

            }
        }
    }

}