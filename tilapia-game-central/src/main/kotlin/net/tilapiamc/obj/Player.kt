package net.tilapiamc.obj

import net.tilapiamc.managers.ServerManager
import net.tilapiamc.obj.game.Game
import net.tilapiamc.communication.PlayerInfo
import java.util.*
import java.util.concurrent.locks.ReentrantLock

class Player(val uuid: UUID,
             val playerName: String,
             val locale: Locale,
             var currentGame: Game? = null,
) {

    var joiningLock = ReentrantLock()

    companion object {
        fun PlayerInfo.toPlayer(serverManager: ServerManager): Player = Player(uniqueId, playerName, locale, serverManager.games[currentGame])
    }

    fun toPlayerInfo(): PlayerInfo = PlayerInfo(playerName, uuid, locale, currentGame?.gameId)


}