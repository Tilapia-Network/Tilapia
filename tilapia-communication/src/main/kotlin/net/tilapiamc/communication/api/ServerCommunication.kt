package net.tilapiamc.communication.api

import java.util.UUID

class ServerCommunication(apiKey: String): TilapiaPrivateAPI(apiKey) {

    fun start(): ServerCommunicationSession {
        TODO()
    }

}

class ServerCommunicationSession(val communication: ServerCommunication, val serverId: UUID, val proxyId: UUID, val sessionId: String) {

}