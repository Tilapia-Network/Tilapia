package net.tilapiamc.api.game.minigame

import net.tilapiamc.api.game.Game
import net.tilapiamc.api.game.GameType
import net.tilapiamc.api.player.LocalNetworkPlayer
import net.tilapiamc.api.player.NetworkPlayer
import net.tilapiamc.api.server.TilapiaServer
import java.util.*
import kotlin.collections.ArrayList

abstract class MiniGame(
    server: TilapiaServer, gameId: UUID, managed: Boolean
): Game(server, GameType.MINIGAME, gameId, managed) {

    val inGamePlayers = ArrayList<NetworkPlayer>()
    val spectatorPlayers = ArrayList<NetworkPlayer>()

}