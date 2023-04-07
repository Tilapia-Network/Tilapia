package net.tilapia.api.game

import net.tilapia.api.player.NetworkPlayer
import java.util.UUID

abstract class Game(
    val gameType: GameType,
    val gameId: UUID,
    val managed: Boolean
) {

    val players = ArrayList<NetworkPlayer>()

}