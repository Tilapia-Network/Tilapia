package net.tilapiamc.proxycore

import com.velocitypowered.api.proxy.Player
import kotlinx.coroutines.runBlocking
import net.tilapiamc.proxyapi.PlayerJoinResult
import net.tilapiamc.proxyapi.game.Game
import net.tilapiamc.proxyapi.player.LocalNetworkPlayer
import net.tilapiamc.proxycore.networking.toGame

class LocalPlayerImpl(val internal: TilapiaProxyCore, proxyPlayer: Player): LocalNetworkPlayer(internal, proxyPlayer) {

    override val nameWithPrefix: String
        get() = "$prefix $playerName"
    override val prefix: String
        get() = "$prefixColor[開發者] "
    override val prefixColor: String
        get() = "&9"

    override val isLocal: Boolean = true
    override fun where(): Game? {
        return runBlocking {
            internal.session.communication.where(uuid).toGame(internal.communication)
        }
    }

    override fun send(game: Game, forceJoin: Boolean, spectate: Boolean): PlayerJoinResult {
        return runBlocking {
            proxyAPI.internal.sendToGame(this@LocalPlayerImpl, game, forceJoin, spectate)
        }
    }


}