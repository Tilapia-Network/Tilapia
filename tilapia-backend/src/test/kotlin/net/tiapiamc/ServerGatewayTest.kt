package net.tiapiamc

import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestCaseOrder
import io.ktor.client.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.serialization.gson.*
import io.ktor.server.testing.*
import net.tiapiamc.config.Config
import net.tilapiamc.common.SuspendEventTarget
import net.tilapiamc.communication.DatabaseLogin
import net.tilapiamc.communication.api.ServerCommunication
import strikt.api.expectThrows

class ServerGatewayTest : StringSpec() {

    init {
        "Unauthorized Request " {
            testApplication {
                application {
                    module()
                }
                expectThrows<Throwable> {
                    val session = ServerCommunication(createClient {
                        clientConfig("AAAA")
                    }).start(listOf("test_database_1", "test_database_2"), { ignoreException -> SuspendEventTarget(ignoreException) })
                    session
                }
            }
        }

        var databaseLogin: DatabaseLogin? = null

        "Normal Session" {
            testApplication {
                application {
                    module()
                }

                val communication = ServerCommunication(createClient {
                    clientConfig(Config.API_KEY)
                })

                val session = communication.start(listOf("test_database_1", "test_database_2"), { ignoreException ->
                    SuspendEventTarget(ignoreException)
                }) {
                    onServerConnected.add {
                        closeSession()
                    }
                }

                databaseLogin = session.databaseLogin
            }

            // TODO: Verify the server is created
        }



    }

    override fun testCaseOrder(): TestCaseOrder {
        return TestCaseOrder.Sequential
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
