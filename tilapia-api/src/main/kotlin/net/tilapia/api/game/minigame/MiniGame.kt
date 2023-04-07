package net.tilapia.api.game.minigame

import net.tilapia.api.game.Game
import net.tilapia.api.game.GameType
import net.tilapia.api.player.LocalNetworkPlayer
import net.tilapia.api.player.NetworkPlayer
import java.util.*
import kotlin.collections.ArrayList

abstract class MiniGame(
    gameId: UUID, managed: Boolean
): Game(GameType.MINIGAME, gameId, managed) {

    val inGamePlayers = ArrayList<NetworkPlayer>()
    val spectatorPlayers = ArrayList<NetworkPlayer>()

}