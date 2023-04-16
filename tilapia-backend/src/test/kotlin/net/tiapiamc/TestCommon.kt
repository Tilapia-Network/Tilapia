package net.tiapiamc

import io.ktor.client.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.serialization.gson.*
import io.ktor.server.testing.*
import net.tiapiamc.config.Config
import net.tiapiamc.managers.ServerManager
import net.tilapiamc.common.SuspendEventTarget
import net.tilapiamc.communication.JoinResult
import net.tilapiamc.communication.PlayerInfo
import net.tilapiamc.communication.api.ProxyCommunication
import net.tilapiamc.communication.api.ProxyCommunicationSession
import net.tilapiamc.communication.api.ServerCommunication
import net.tilapiamc.communication.api.ServerCommunicationSession
import strikt.api.expectThat
import strikt.assertions.isEmpty
import strikt.assertions.isNotEmpty
import strikt.assertions.isNull
import java.util.*


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

fun withServerContext(serverManager: ServerManager, getPlayerJoinResult: (PlayerInfo, UUID, Boolean) -> JoinResult = { _, _, _ -> JoinResult(true, 1.0, "") }, block: suspend ServerCommunicationSession.(ProxyCommunicationSession) -> Unit) {
    // TODO: Fix #2
    testApplication {
        application {
            module(serverManager)
        }

        val proxyCommunication = ProxyCommunication(createClient {
            clientConfig(Config.API_KEY)
        })

        val communication = ServerCommunication(createClient {
            clientConfig(Config.API_KEY)
        })
        val closeReason = proxyCommunication.start(listOf(), { SuspendEventTarget(it) }) {
            onProxyConnected.add { proxyCommunicationSession ->
                Thread.sleep(100)
                try {
                    val closeReason = communication.start(listOf("test_database_1"), { ignoreException ->
                        SuspendEventTarget(ignoreException)
                    }, getPlayerJoinResult = getPlayerJoinResult) {
                        onServerConnected.add {
                            try {
                                expectThat(communication.listServers())
                                    .isNotEmpty()
                                expectThat(communication.listServers(serverIdPrefix = "o"))
                                    .isEmpty()
                                expectThat(communication.listServers(serverIdPrefix = serverId.toString()))
                                    .isNotEmpty()

                                block(proxyCommunicationSession.session)
                            } finally {
                                closeSession()
                            }
                        }
                    }
                    expectThat(closeReason)
                        .isNull()
                    expectThat(communication.listServers())
                        .isEmpty()
                } finally {
                    closeSession()
                }
            }
        }
        expectThat(closeReason)
            .isNull()

    }
}