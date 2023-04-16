package net.tiapiamc

import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestCaseOrder
import io.ktor.server.testing.*
import net.tiapiamc.config.Config
import net.tiapiamc.managers.ServerManager
import net.tilapiamc.common.SuspendEventTarget
import net.tilapiamc.communication.GameType
import net.tilapiamc.communication.JoinResult
import net.tilapiamc.communication.MiniGameInfo
import net.tilapiamc.communication.PlayerInfo
import net.tilapiamc.communication.api.ProxyCommunication
import net.tilapiamc.communication.api.ProxyCommunicationSession
import net.tilapiamc.communication.api.ServerCommunication
import net.tilapiamc.communication.api.ServerCommunicationSession
import strikt.api.expectThat
import strikt.assertions.*
import java.util.*

class GameServiceTest: StringSpec() {

    init {
        "Create Game and Check Game Existence" {
            withServerContext(ServerManager()) {
                val gameInfo = MiniGameInfo(serverId, UUID.randomUUID(), "main", arrayListOf(), "fleetwars")
                expectThat(communication.getTypes(GameType.MINIGAME))
                    .isEmpty()
                expectThat(communication.getGames())
                    .isEmpty()
                communication.registerGame(gameInfo)
                expectThat(communication.getGames())
                    .first().get { miniGame }.isNotNull().get { gameId }.isEqualTo(gameInfo.gameId)
                expectThat(communication.getTypes(GameType.LOBBY))
                    .isEmpty()
                expectThat(communication.getTypes(GameType.MINIGAME))
                    .first().isEqualTo("fleetwars")
                expectThat(communication.getTypes())
                    .first().isEqualTo("fleetwars")
            }
        }
        "Player Join Result" {
            withServerContext(ServerManager(), { player, uuid -> JoinResult(true, 8.7, "${player.uniqueId} - $uuid") }) {
                val gameInfo = MiniGameInfo(serverId, UUID.randomUUID(), "main", arrayListOf(), "fleetwars")
                expectThat(communication.getTypes(GameType.MINIGAME))
                    .isEmpty()
                expectThat(communication.getGames())
                    .isEmpty()

                communication.registerGame(gameInfo)

                expectThat(communication.getGames())
                    .first().get { miniGame }.isNotNull().get { gameId }.isEqualTo(gameInfo.gameId)
                expectThat(communication.getTypes(GameType.LOBBY))
                    .isEmpty()
                expectThat(communication.getTypes(GameType.MINIGAME))
                    .first().isEqualTo("fleetwars")
                expectThat(communication.getTypes())
                    .first().isEqualTo("fleetwars")

                val playerId = UUID.randomUUID()
                it.login(PlayerInfo("fan87", playerId, Locale.TRADITIONAL_CHINESE, null))


                val result = communication.getGamesForPlayer(playerId)
                expectThat(result.entries).isNotEmpty()
                expectThat(result.values).first().get { this.chance }.isEqualTo(8.7)
                expectThat(result.values).first().get { this.success }.isTrue()
                expectThat(result.values).first().get { this.message }.isEqualTo("$playerId - ${gameInfo.gameId}")
                it.logout(playerId)

                communication.endGame(gameInfo)

            }
        }
    }

    override fun testCaseOrder(): TestCaseOrder {
        return TestCaseOrder.Sequential
    }


    fun withServerContext(serverManager: ServerManager, getPlayerJoinResult: (PlayerInfo, UUID) -> JoinResult = { _, _ -> JoinResult(true, 1.0, "") }, block: suspend ServerCommunicationSession.(ProxyCommunicationSession) -> Unit) {
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
                        }, getPlayerJoinResult = { player, gameId ->
                               JoinResult(true, 8.7, "${player.uniqueId} - $gameId")
                        }) {
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

}