package net.tilapiamc.core.networking

import kotlinx.coroutines.runBlocking
import net.tilapiamc.api.TilapiaCore
import net.tilapiamc.api.game.Game
import net.tilapiamc.api.player.NetworkPlayer
import net.tilapiamc.communication.PlayerInfo
import net.tilapiamc.communication.api.ServerCommunicationSession
import java.util.*

class NetworkPlayerImpl(val session: ServerCommunicationSession, val data: PlayerInfo): NetworkPlayer(
    TilapiaCore.instance,
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
            session.communication.where(uuid).toGame(session)
        }
    }

    override fun send(game: Game, forceJoin: Boolean, spectate: Boolean) {
        return runBlocking {
            tilapiaCore.getInternal().sendToGame(this@NetworkPlayerImpl, game, forceJoin, spectate)
        }
    }

    override fun equals(other: Any?): Boolean {
        return other is NetworkPlayer && other.uuid == this.uuid
    }

    override fun hashCode(): Int {
        return uuid.hashCode()
    }
}