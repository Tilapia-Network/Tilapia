package net.tilapiamc.communication.api

import net.tilapiamc.communication.GameData
import net.tilapiamc.communication.ProxyInfo
import net.tilapiamc.communication.ServerInfo
import java.util.UUID

class ServerCommunication(val apiKey: String) {

    fun where(player: UUID): GameData {

    }

    fun getServerInfo(serverId: UUID): ServerInfo? {

    }

    fun getProxyInfo(proxyId: UUID): ProxyInfo? {

    }

    fun start(): ServerCommunicationSession {

    }
}

class ServerCommunicationSession(val communication: ServerCommunication, val serverId: UUID, val proxyId: UUID, val sessionId: String) {

}