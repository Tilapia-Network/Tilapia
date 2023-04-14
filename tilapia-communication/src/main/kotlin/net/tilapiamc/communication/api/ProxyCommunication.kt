package net.tilapiamc.communication.api

import java.util.*

class ProxyCommunication(val apiKey: String) {
}

class ProxyCommunicationSession(val communication: ProxyCommunication, val sessionId: String) {
    fun login(player: UUID) {
        TODO()

    }
    fun logout(player: UUID) {
        TODO()

    }
}