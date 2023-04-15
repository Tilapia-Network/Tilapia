package net.tiapiamc

import io.kotest.core.spec.style.StringSpec
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
import net.tilapiamc.communication.api.ProxyCommunication
import net.tilapiamc.communication.api.ServerCommunication
import strikt.api.expectThat
import strikt.assertions.isEmpty
import strikt.assertions.isNotEmpty
import strikt.assertions.isNotNull
import strikt.assertions.isNull

class ServerServiceTest: StringSpec() {
    val serverManager = ServerManager()

    init {
        "Create Server without Proxy" {
            testApplication {
                application {
                    module(serverManager)
                }

                val communication = ServerCommunication(createClient {
                    clientConfig(Config.API_KEY)
                })


                val closeReason = communication.start(listOf("test_database_1"), { ignoreException ->
                    SuspendEventTarget(ignoreException)
                }) {
                    onServerConnected.add {
                        closeSession()
                    }
                }
                expectThat(closeReason)
                    .isNotNull()
            }
        }
        "Create Server" {
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
                    onProxyConnected.add {
                        val closeReason = communication.start(listOf("test_database_1"), { ignoreException ->
                            SuspendEventTarget(ignoreException)
                        }) {
                            onServerConnected.add {
                                expectThat(communication.listServers())
                                    .isNotEmpty()
                                expectThat(communication.listServers(serverIdPrefix = "o"))
                                    .isEmpty()
                                expectThat(communication.listServers(serverIdPrefix = serverId.toString()))
                                    .isNotEmpty()
                                closeSession()
                            }
                        }
                        expectThat(closeReason)
                            .isNull()
                        expectThat(communication.listServers())
                            .isEmpty()
                        closeSession()
                    }
                }


                expectThat(closeReason)
                    .isNull()

            }
        }

    }

    fun HttpClientConfig<*>.clientConfig(apiKey: String) {
        install(WebSockets) {
            timeout = 15000
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

}