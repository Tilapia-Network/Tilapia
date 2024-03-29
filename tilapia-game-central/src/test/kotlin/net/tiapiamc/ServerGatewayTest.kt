package net.tiapiamc

import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestCaseOrder
import io.ktor.server.testing.*
import net.tilapiamc.config.Config
import net.tilapiamc.managers.ServerManager
import net.tilapiamc.common.SuspendEventTarget
import net.tilapiamc.communication.DatabaseLogin
import net.tilapiamc.communication.api.ProxyCommunication
import net.tilapiamc.communication.api.ServerCommunication
import net.tilapiamc.module
import strikt.api.expect
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.isNotNull
import strikt.assertions.isNull
import java.sql.DriverManager

class ServerGatewayTest : StringSpec() {

    init {
        "Unauthorized Request " {
            testApplication {
                application {
                    module(ServerManager())
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
                    module(ServerManager())
                }

                val communication = ProxyCommunication(createClient {
                    clientConfig(Config.API_KEY)
                })

                expectThat(communication.start(listOf("test_database_1", "test_database_2"), { ignoreException ->
                    SuspendEventTarget(ignoreException)
                }) {
                    onProxyConnected.add {
                        databaseLogin = it.session.databaseLogin
                        closeSession()
                    }
                }).isNull()

            }

            // TODO: Verify the server is created
        }

        "Verify Login is Valid" {
            testApplication {
                application {
                    module(ServerManager())
                }

                val communication = ProxyCommunication(createClient {
                    clientConfig(Config.API_KEY)
                })

                expectThat(communication.start(listOf("test_database_1", "test_database_2"), { ignoreException ->
                    SuspendEventTarget(ignoreException)
                }) {
                    onProxyConnected.add {
                        try {
                            databaseLogin = it.session.databaseLogin
                            expect {
                                DriverManager.getConnection(Config.DATABASE_URL, it.session.databaseLogin.username, it.session.databaseLogin.password)
                            }
                        } finally {
                            closeSession()
                        }
                    }
                }).isNull()

            }

            // TODO: Verify the server is created
        }

        "Verify Login Is Invalid After Close" {
            expectThat(databaseLogin)
                .isNotNull()
            expectThrows<Throwable> {
                DriverManager.getConnection(Config.DATABASE_URL, databaseLogin!!.username, databaseLogin!!.password)
            }
        }



    }

    override fun testCaseOrder(): TestCaseOrder {
        return TestCaseOrder.Sequential
    }

}
