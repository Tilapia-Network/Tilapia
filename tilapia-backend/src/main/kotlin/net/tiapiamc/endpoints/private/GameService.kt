package net.tiapiamc.endpoints.private

import com.google.gson.Gson
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import net.tiapiamc.managers.ServerManager

object GameService {

    fun Application.applyGameService(serverManager: ServerManager, gson: Gson) {
        routing {
            authenticate("private-api") {
                post("/game/register") {
                    // Requires Session ID
                }
                get("/game/list") {
                    // game type
                    // game ID starts with
                    // type
                }
                get("/game/minigame/list") {
                    // game ID starts with
                    // type
                }
                get("/game/lobby/list") {
                    // game ID starts with
                    // type
                }
                get("/game/minigame/for-player") {
                    // game ID starts with
                    // type
                }
                get("/game/lobby/for-player") {
                    // game ID starts with
                    // type
                }
                get("/game/minigame/types") {

                }
                get("/game/lobby/types") {

                }
            }
        }
    }

}