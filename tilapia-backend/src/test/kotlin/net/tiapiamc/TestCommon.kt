package net.tiapiamc

import io.ktor.client.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.serialization.gson.*



fun HttpClientConfig<*>.clientConfig(apiKey: String) {
    install(WebSockets) {
        maxFrameSize = Long.MAX_VALUE
        pingInterval = 15000
    }
    install(ContentNegotiation) {
        gson()
    }
    install(Auth) {
        bearer {
            loadTokens {
                BearerTokens(apiKey, "")
            }
        }
    }
}