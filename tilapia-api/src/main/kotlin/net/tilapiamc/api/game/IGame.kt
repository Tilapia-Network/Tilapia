package net.tilapiamc.api.game

import net.tilapia.api.player.NetworkPlayer
import net.tilapia.api.server.TilapiaServer
import java.util.*

interface IGame {

    val server: TilapiaServer
    val gameType: GameType
    val gameId: UUID
    val managed: Boolean
    val players: ArrayList<NetworkPlayer>


}