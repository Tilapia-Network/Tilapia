package net.tilapiamc.proxycore.networking

import net.tilapiamc.communication.GameData
import net.tilapiamc.communication.api.ProxyCommunication
import net.tilapiamc.proxyapi.game.Game

fun GameData.toGame(communication: ProxyCommunication): Game? {
    if (this.lobby != null) {
        return NetworkLobbyImpl(communication, this.lobby!!)
    }
    if (this.miniGame != null) {
        return NetworkMiniGameImpl(communication, this.miniGame!!)
    }
    return null
}