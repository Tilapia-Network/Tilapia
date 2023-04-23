package net.tilapiamc.proxycore.networking

import net.tilapiamc.communication.GameData
import net.tilapiamc.communication.api.ProxyCommunicationSession
import net.tilapiamc.proxyapi.game.Game

fun GameData.toGame(session: ProxyCommunicationSession): Game? {
    if (this.lobby != null) {
        return NetworkLobbyImpl(session, this.lobby!!)
    }
    if (this.miniGame != null) {
        return NetworkMiniGameImpl(session, this.miniGame!!)
    }
    return null
}