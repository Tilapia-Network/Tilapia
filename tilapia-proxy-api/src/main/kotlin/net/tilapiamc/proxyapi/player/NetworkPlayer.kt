package net.tilapiamc.proxyapi.player

import net.tilapiamc.proxyapi.PlayerJoinResult
import net.tilapiamc.proxyapi.TilapiaProxyAPI
import net.tilapiamc.proxyapi.game.Game
import java.util.*

abstract class NetworkPlayer(
    open val proxyAPI: TilapiaProxyAPI,
    open val playerName: String,
    open val language: Locale,
    open val uuid: UUID,
) {

    abstract val isLocal: Boolean

    abstract fun where(): Game?
    abstract fun send(game: Game, forceJoin: Boolean, spectate: Boolean): PlayerJoinResult


}