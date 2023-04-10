package net.tilapiamc.api.player

import net.tilapiamc.api.TilapiaCore
import net.tilapiamc.api.game.Game
import net.tilapiamc.api.game.GamesManager
import net.tilapiamc.api.game.lobby.Lobby
import net.tilapiamc.api.game.minigame.MiniGame
import net.tilapiamc.api.server.TilapiaServer
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
        get() = if (currentGameId == null) null else tilapiaCore.gamesManager.getGameById(currentGameId!!)

    fun findMiniGameToJoin(gameType: String): MiniGame? {
        return tilapiaCore.getInternal().findMiniGameToJoin(this, gameType)

    }

    fun findLobbyToJoin(lobbyType: String): Lobby? {
        return tilapiaCore.getInternal().findLobbyToJoin(this, lobbyType)
    }

    fun sendToGame(game: Game?) {
        tilapiaCore.getInternal().sendToGame(this, game)
    }

}