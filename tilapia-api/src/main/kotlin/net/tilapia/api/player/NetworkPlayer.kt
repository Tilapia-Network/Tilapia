package net.tilapia.api.player

import net.tilapia.api.TilapiaCore
import net.tilapia.api.game.Game
import net.tilapia.api.game.GamesManager
import net.tilapia.api.server.TilapiaServer
import java.util.UUID

abstract class NetworkPlayer(
    val tilapiaCore: TilapiaCore,
    val name: String,
    val uuid: UUID,
) {

    val currentServer: TilapiaServer
        get() = currentGame.server
    lateinit var currentGameId: UUID
    val currentGame: Game
        get() = GamesManager.getGameById(currentGameId)!!

    fun sendToGame(game: Game) {
        tilapiaCore.getInternal().sendToGame(this, game)
    }

}