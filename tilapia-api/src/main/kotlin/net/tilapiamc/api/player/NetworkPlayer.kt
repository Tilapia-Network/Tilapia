package net.tilapiamc.api.player

import net.tilapiamc.api.TilapiaCore
import net.tilapiamc.api.game.Game
import net.tilapiamc.api.game.GamesManager
import net.tilapiamc.api.server.TilapiaServer
import java.util.UUID

abstract class NetworkPlayer(
    val tilapiaCore: net.tilapiamc.api.TilapiaCore,
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