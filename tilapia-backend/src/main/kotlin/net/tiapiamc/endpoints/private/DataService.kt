package net.tiapiamc.endpoints.private

import io.ktor.server.application.*
import io.ktor.server.routing.*

object DataService {

    fun Application.applyDataService() {
        routing {
            get("/data/getDatabase") {

            }
        }
    }

}