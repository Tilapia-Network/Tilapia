package net.tilapiamc.communication.api

import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import net.tilapiamc.common.SuspendEventTarget
import net.tilapiamc.communication.DatabaseLogin
import net.tilapiamc.communication.session.PacketRegistry
import net.tilapiamc.communication.session.Session
import net.tilapiamc.communication.session.SessionEvent
import net.tilapiamc.communication.session.client.server.CPacketServerHandShake
import net.tilapiamc.communication.session.server.SPacketDatabaseLogin
import net.tilapiamc.communication.session.server.server.SPacketServerHandShake
import java.util.*

class ServerCommunication(client: HttpClient): TilapiaPrivateAPI(client) {

    suspend fun start(requiredSchemas: List<String>,
              eventTargetFactory: (ignoreException: Boolean) -> SuspendEventTarget<out SessionEvent>,
              block: suspend ServerCommunicationSession.() -> Unit = {}
    ): CloseReason? {
        var reason: CloseReason? = null
        client.webSocket("/server/gateway") {
            ServerCommunicationSession(requiredSchemas, this@ServerCommunication, eventTargetFactory, this).also {
                it.block()
                it.onSessionClosed.add {
                    if (!it.closedBySelf) {
                        reason = it.closeReason
                    }
                }
            }.startSession()
        }
        return reason
    }

}

class ServerCommunicationSession(requiredSchemas: List<String>,
                                val communication: ServerCommunication,
                                eventTargetFactory: (ignoreException: Boolean) -> SuspendEventTarget<out SessionEvent>,
                                websocketSession: DefaultWebSocketSession
): Session(PacketRegistry.serverPackets, eventTargetFactory, websocketSession) {

    val onServerConnected = eventTargetFactory(false) as SuspendEventTarget<ServerConnectedEvent>

    lateinit var serverId: UUID
    lateinit var proxyId: UUID
    lateinit var databaseLogin: DatabaseLogin

    init {
        onSessionStarted.add {

            val handShake = waitForPacketWithType<SPacketServerHandShake>()?:clientError("Not receiving handshake packet")
            proxyId = handShake.proxyId
            serverId = handShake.serverId
            sendPacket(CPacketServerHandShake(requiredSchemas))
            val databasePacket = waitForPacketWithType<SPacketDatabaseLogin>()?:clientError("Not receiving database login packet")
            databaseLogin = databasePacket.databaseLogin
            onServerConnected(ServerConnectedEvent(this))
        }
    }


    class ServerConnectedEvent(override val session: ServerCommunicationSession): SessionEvent(session)

}