package net.tilapiamc.communication.api

import com.google.gson.JsonPrimitive
import net.tilapiamc.communication.GameData
import java.util.*
import kotlin.collections.HashMap

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