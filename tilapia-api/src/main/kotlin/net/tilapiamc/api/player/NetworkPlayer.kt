package net.tilapiamc.api.player

import net.tilapiamc.api.TilapiaCore
import net.tilapiamc.api.game.Game
import net.tilapiamc.api.game.lobby.Lobby
import net.tilapiamc.api.game.minigame.MiniGame
import net.tilapiamc.api.language.LanguageBundle
import net.tilapiamc.api.server.TilapiaServer
import java.util.*

abstract class NetworkPlayer(
    val tilapiaCore: TilapiaCore,
    val playerName: String,
    val uuid: UUID,
) {

    open val isLocal: Boolean = false

    lateinit var currentServer: TilapiaServer
    var currentGameId: UUID? = null
    val currentGame: Game?
        get() = if (currentGameId == null) null else tilapiaCore.localGameManager.getLocalGameById(currentGameId!!) as Game
    abstract val language: Locale
    fun getLanguageBundle(): LanguageBundle {
        return tilapiaCore.languageManager.getLanguageBundle(language)
    }
    fun findMiniGameToJoin(gameType: String, forceJoin: Boolean = false): MiniGame? {
        return tilapiaCore.gameFinder.findMiniGameToJoin(this, gameType, forceJoin)

    }

    fun findLobbyToJoin(lobbyType: String, forceJoin: Boolean = false): Lobby? {
        return tilapiaCore.gameFinder.findLobbyToJoin(this, lobbyType, forceJoin)
    }

    fun sendToGame(game: Game?, forceJoin: Boolean = false, spectate: Boolean = false) {
        tilapiaCore.getInternal().sendToGame(this, game, forceJoin, spectate)
    }

    abstract fun where(): Game

}