package net.tilapiamc.proxyapi.player

import com.velocitypowered.api.proxy.Player
import net.tilapiamc.common.language.LanguageBundle
import net.tilapiamc.proxyapi.TilapiaProxyAPI
import net.tilapiamc.proxyapi.game.Game
import net.tilapiamc.proxyapi.game.Lobby
import net.tilapiamc.proxyapi.game.MiniGame
import java.util.*

abstract class LocalNetworkPlayer(proxyAPI: TilapiaProxyAPI,
                                  val proxyPlayer: Player
): NetworkPlayer(proxyAPI, proxyPlayer.username, Locale.TRADITIONAL_CHINESE, proxyPlayer.uniqueId), Player by proxyPlayer {

    var currentGameId: UUID? = null

    fun getLanguageBundle(): LanguageBundle {
        return proxyAPI.languageManager.getLanguageBundle(language)
    }
    fun findMiniGameToJoin(gameType: String, forceJoin: Boolean = false): MiniGame? {
        return proxyAPI.gameFinder.findMiniGameToJoin(this, gameType, forceJoin)
    }

    fun findLobbyToJoin(lobbyType: String, forceJoin: Boolean = false): Lobby? {
        return proxyAPI.gameFinder.findLobbyToJoin(this, lobbyType, forceJoin)
    }

    fun sendToGame(game: Game, forceJoin: Boolean = false, spectate: Boolean = false) {
        proxyAPI.internal.sendToGame(this, game, forceJoin, spectate)
    }

    abstract fun where(): Game?

}