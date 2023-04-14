package net.tiapiamc.endpoints.private

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*

object DataService {

    fun Application.applyDataService() {
        routing {
            post("/data/getPlayerData") {

            }
            post("/data/setPlayerData") {

            }
            post("/player/getLeaderBoard") {

            }
        }
    }

}