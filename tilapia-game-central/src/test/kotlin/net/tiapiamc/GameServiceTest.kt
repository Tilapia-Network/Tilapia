package net.tiapiamc

import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestCaseOrder
import net.tiapiamc.managers.ServerManager
import net.tilapiamc.communication.GameType
import net.tilapiamc.communication.JoinResult
import net.tilapiamc.communication.MiniGameInfo
import net.tilapiamc.communication.PlayerInfo
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
            withServerContext(ServerManager(), { player, uuid, forceJoin -> JoinResult(true, 8.7, "${player.uniqueId} - $uuid") }) {
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

                communication.endGame(gameInfo.gameId)

            }
        }
    }

    override fun testCaseOrder(): TestCaseOrder {
        return TestCaseOrder.Sequential
    }




}