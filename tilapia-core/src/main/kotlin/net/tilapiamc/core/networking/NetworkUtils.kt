package net.tilapiamc.core.networking

import net.tilapiamc.api.game.Game
import net.tilapiamc.communication.GameData
import net.tilapiamc.communication.api.ServerCommunicationSession

fun GameData.toGame(session: ServerCommunicationSession): Game? {
    if (this.lobby != null) {
        return NetworkLobbyImpl(session, this.lobby!!)
    }
    if (this.miniGame != null) {
        return NetworkMiniGameImpl(session, this.miniGame!!)
    }
    return null
}