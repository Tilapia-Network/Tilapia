package net.tiapiamc.obj

import net.tiapiamc.obj.game.Game
import net.tilapiamc.communication.PlayerInfo
import java.util.*

class Player(val uuid: UUID,
             val playerName: String,
             val locale: Locale,
             var currentGame: Game,
) {

    fun toPlayerInfo(): PlayerInfo = PlayerInfo(playerName, uuid, locale, currentGame.gameId)


}