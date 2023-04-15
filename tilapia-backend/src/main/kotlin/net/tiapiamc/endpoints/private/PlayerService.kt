package net.tiapiamc.endpoints.private

import com.google.gson.Gson
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import net.tiapiamc.managers.ServerManager

object PlayerService {

    fun Application.applyPlayerService(serverManager: ServerManager, gson: Gson) {
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