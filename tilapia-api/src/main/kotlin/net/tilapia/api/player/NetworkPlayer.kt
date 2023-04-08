package net.tilapia.api.player

import net.tilapia.api.TilapiaCore
import net.tilapia.api.game.Game
import net.tilapia.api.game.GamesManager
import net.tilapia.api.server.TilapiaServer
import java.util.UUID

abstract class NetworkPlayer(
    val tilapiaCore: TilapiaCore,
    val playerName: String,
    val uuid: UUID,
) {

    open val isLocal: Boolean = false

    lateinit var currentServer: TilapiaServer
    var currentGameId: UUID? = null
    val currentGame: Game?
        get() = if (currentGameId == null) null else GamesManager.getGameById(currentGameId!!)

    fun sendToGame(game: Game?) {
        tilapiaCore.getInternal().sendToGame(this, game)
    }

}