package net.tiapiamc.endpoints.private

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*

object ProxyService {

    fun Application.applyProxyService() {
        routing {
            get("/proxy/list") {

            }
            get("/proxy/info") {

            }
            webSocket("/proxy/gateway") {

            }
        }
    }

}