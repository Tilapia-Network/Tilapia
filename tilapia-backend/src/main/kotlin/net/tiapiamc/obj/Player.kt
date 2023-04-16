package net.tiapiamc.obj

import net.tiapiamc.managers.ServerManager
import net.tiapiamc.obj.game.Game
import net.tilapiamc.communication.PlayerInfo
import java.util.*

class Player(val uuid: UUID,
             val playerName: String,
             val locale: Locale,
             var currentGame: Game? = null,
) {

    companion object {
        fun PlayerInfo.toPlayer(serverManager: ServerManager): Player = Player(uniqueId, playerName, locale, serverManager.games[currentGame])
    }

    fun toPlayerInfo(): PlayerInfo = PlayerInfo(playerName, uuid, locale, currentGame?.gameId)


}