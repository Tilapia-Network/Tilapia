package net.tiapiamc

import io.kotest.core.spec.style.StringSpec
import net.tiapiamc.managers.ServerManager
import net.tilapiamc.communication.JoinResult
import net.tilapiamc.communication.MiniGameInfo
import net.tilapiamc.communication.PlayerInfo
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isNotEmpty
import strikt.assertions.isNotNull
import java.util.*

class PlayerSendingTest: StringSpec() {

    init {
        "Send Player and Check Location" {
            withServerContext(ServerManager(), { _, _, _ -> JoinResult(true, 1.0, "") }) { proxySession ->
                val playerInfo = PlayerInfo("fan87", UUID.randomUUID(), Locale.TRADITIONAL_CHINESE, null)
                val gameInfo = MiniGameInfo(serverId, UUID.randomUUID(), "main", listOf(), "fleetwars")
                proxySession.login(playerInfo)
                communication.registerGame(gameInfo)
                communication.send(playerInfo.uniqueId, gameInfo.gameId)
                expectThat(communication.where(playerInfo.uniqueId))
                    .isNotNull()
                    .get { miniGame }.isNotNull().also {
                        it.get { gameId }.isEqualTo(gameInfo.gameId)
                        it.get { players }.isNotEmpty()
                    }

                proxySession.logout(playerInfo.uniqueId)
                communication.endGame(gameInfo.gameId)
            }
        }
    }

}