package net.tilapiamc.core.networking

import net.tilapiamc.api.TilapiaCore
import net.tilapiamc.api.game.Game
import net.tilapiamc.api.player.NetworkPlayer
import net.tilapiamc.communication.PlayerInfo
import net.tilapiamc.communication.api.ServerCommunicationSession
import java.util.*

class NetworkPlayerImpl(val session: ServerCommunicationSession, val data: PlayerInfo): NetworkPlayer(
    TilapiaCore.instance,
    data.playerName,
    data.uniqueId
) {
    override val isLocal: Boolean
        get() = false
    override val language: Locale
        get() = data.locale

    override fun where(): Game {
        return session.communication.where(uuid).toGame(session.communication)!!
    }

    override fun equals(other: Any?): Boolean {
        return other is NetworkPlayer && other.uuid == this.uuid
    }

    override fun hashCode(): Int {
        return uuid.hashCode()
    }
}