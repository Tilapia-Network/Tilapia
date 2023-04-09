package net.tilapiamc.api.game

import net.tilapiamc.api.player.NetworkPlayer
import net.tilapiamc.api.server.TilapiaServer
import java.util.*

interface IGame {

    val server: TilapiaServer
    val gameType: GameType
    val gameId: UUID
    val managed: Boolean
    val players: ArrayList<NetworkPlayer>


}