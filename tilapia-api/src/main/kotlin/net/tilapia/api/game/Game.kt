package net.tilapia.api.game

import net.tilapia.api.player.NetworkPlayer
import net.tilapia.api.server.TilapiaServer
import java.util.UUID

abstract class Game(
    val server: TilapiaServer,
    val gameType: GameType,
    val gameId: UUID,
    val managed: Boolean
) {

    val players = ArrayList<NetworkPlayer>()

}