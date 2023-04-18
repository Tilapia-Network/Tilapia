package net.tilapiamc.proxycore

import com.velocitypowered.api.proxy.Player
import kotlinx.coroutines.runBlocking
import net.tilapiamc.proxyapi.game.Game
import net.tilapiamc.proxyapi.player.LocalNetworkPlayer
import net.tilapiamc.proxycore.networking.toGame

class LocalPlayerImpl(val internal: TilapiaProxyCore, proxyPlayer: Player): LocalNetworkPlayer(internal, proxyPlayer) {
    override val isLocal: Boolean = true
    override fun where(): Game? {
        return runBlocking {
            internal.session.communication.where(uuid).toGame(internal.communication)
        }
    }

    override fun send(game: Game, forceJoin: Boolean, spectate: Boolean) {
        return runBlocking {
            proxyAPI.internal.sendToGame(this@LocalPlayerImpl, game, forceJoin, spectate)
        }
    }


}