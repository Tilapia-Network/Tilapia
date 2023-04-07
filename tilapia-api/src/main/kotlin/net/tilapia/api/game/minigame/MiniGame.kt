package net.tilapia.api.game.minigame

import net.tilapia.api.game.Game
import net.tilapia.api.game.GameType
import net.tilapia.api.player.LocalNetworkPlayer
import net.tilapia.api.player.NetworkPlayer
import net.tilapia.api.server.TilapiaServer
import java.util.*
import kotlin.collections.ArrayList

abstract class MiniGame(
    server: TilapiaServer, gameId: UUID, managed: Boolean
): Game(server, GameType.MINIGAME, gameId, managed) {

    val inGamePlayers = ArrayList<NetworkPlayer>()
    val spectatorPlayers = ArrayList<NetworkPlayer>()

}