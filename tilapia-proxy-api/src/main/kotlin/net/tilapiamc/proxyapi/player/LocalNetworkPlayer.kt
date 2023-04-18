package net.tilapiamc.proxyapi.player

import com.velocitypowered.api.proxy.Player
import net.tilapiamc.common.language.LanguageBundle
import net.tilapiamc.proxyapi.TilapiaProxyAPI
import net.tilapiamc.proxyapi.game.Lobby
import net.tilapiamc.proxyapi.game.MiniGame
import org.slf4j.LoggerFactory
import java.util.*

abstract class LocalNetworkPlayer(proxyAPI: TilapiaProxyAPI,
                                  val proxyPlayer: Player
): NetworkPlayer(proxyAPI, proxyPlayer.username, Locale.TRADITIONAL_CHINESE, proxyPlayer.uniqueId), Player by proxyPlayer {

    val logger = LoggerFactory.getLogger("PlayerLogger ${proxyPlayer.username}")

    fun getLanguageBundle(): LanguageBundle {
        return proxyAPI.languageManager.getLanguageBundle(language)
    }
    fun findMiniGameToJoin(gameType: String, forceJoin: Boolean = false): MiniGame? {
        return proxyAPI.gameFinder.findMiniGameToJoin(this, gameType, forceJoin)
    }

    fun findLobbyToJoin(lobbyType: String, forceJoin: Boolean = false): Lobby? {
        return proxyAPI.gameFinder.findLobbyToJoin(this, lobbyType, forceJoin)
    }



}