package net.tilapiamc.core.networking

import net.tilapiamc.api.game.Game
import net.tilapiamc.communication.GameData
import net.tilapiamc.communication.api.ServerCommunication

fun GameData.toGame(communication: ServerCommunication): Game? {
    if (this.lobby != null) {
        return NetworkLobbyImpl(communication, this.lobby!!)
    }
    if (this.miniGame != null) {
        return NetworkMiniGameImpl(communication, this.miniGame!!)
    }
    return null
}