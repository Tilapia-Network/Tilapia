package net.tilapiamc.proxycore.networking

import kotlinx.coroutines.runBlocking
import net.tilapiamc.communication.PlayerInfo
import net.tilapiamc.communication.api.ProxyCommunicationSession
import net.tilapiamc.proxyapi.TilapiaProxyAPI
import net.tilapiamc.proxyapi.game.Game
import net.tilapiamc.proxyapi.player.NetworkPlayer
import java.util.*

class NetworkPlayerImpl(val session: ProxyCommunicationSession, val data: PlayerInfo): NetworkPlayer(
    TilapiaProxyAPI.instance,
    data.playerName,
    data.locale,
    data.uniqueId
) {
    override val isLocal: Boolean
        get() = false
    override val language: Locale
        get() = data.locale

    override fun where(): Game? {
        return runBlocking {
            session.communication.where(uuid).toGame(session.communication)
        }
    }

    override fun send(game: Game, forceJoin: Boolean, spectate: Boolean) {
        return runBlocking {
            proxyAPI.internal.sendToGame(this@NetworkPlayerImpl, game, forceJoin, spectate)
        }
    }

    override fun equals(other: Any?): Boolean {
        return other is NetworkPlayer && other.uuid == this.uuid
    }

    override fun hashCode(): Int {
        return uuid.hashCode()
    }
}