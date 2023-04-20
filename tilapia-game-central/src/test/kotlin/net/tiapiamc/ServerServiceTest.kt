package net.tiapiamc

import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestCaseOrder
import io.ktor.server.testing.*
import net.tilapiamc.config.Config
import net.tilapiamc.managers.ServerManager
import net.tilapiamc.common.SuspendEventTarget
import net.tilapiamc.communication.api.ProxyCommunication
import net.tilapiamc.communication.api.ServerCommunication
import net.tilapiamc.module
import strikt.api.expectThat
import strikt.assertions.*

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
                        Thread.sleep(100)
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

        "Check Create Server Packet" {
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
                        Thread.sleep(100)
                        var receivedConnect = false
                        var receivedDisconnect = false
                        onServerAdded.add {
                            receivedConnect = true
                        }
                        onServerRemoved.add {
                            receivedDisconnect = true
                        }
                        val closeReason = communication.start(listOf("test_database_1"), { ignoreException ->
                            SuspendEventTarget(ignoreException)
                        }) {
                            onServerConnected.add {
                                closeSession()
                            }
                        }
                        closeSession()
                        expectThat(receivedConnect).isTrue()
                        expectThat(receivedDisconnect).isTrue()
                    }
                }
                expectThat(closeReason)
                    .isNull()

            }
        }

    }

    override fun testCaseOrder(): TestCaseOrder? {
        return TestCaseOrder.Sequential
    }
}