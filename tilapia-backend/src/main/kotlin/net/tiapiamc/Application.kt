package net.tiapiamc

import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.forwardedheaders.*
import net.tiapiamc.config.Config
import net.tiapiamc.endpoints.private.GameService.applyGameService
import net.tiapiamc.endpoints.private.PlayerService.applyPlayerService
import net.tiapiamc.endpoints.private.ProxyService.applyProxyService
import net.tiapiamc.endpoints.private.ServerService.applyServerService

fun main() {
    embeddedServer(Netty, port = Config.PORT, host = Config.HOST, module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(ForwardedHeaders)
    install(XForwardedHeaders)
    install(AutoHeadResponse)
    authentication {
        bearer("private-api") {
            realm = "private-api"
            authenticate {
                if (it.token == Config.API_KEY) {
                    UserIdPrincipal("private-api")
                } else {
                    null
                }
            }
        }
    }
    install(ContentNegotiation) {
        gson {

        }
    }

    applyProxyService()
    applyPlayerService()
    applyServerService()
    applyGameService()
}
