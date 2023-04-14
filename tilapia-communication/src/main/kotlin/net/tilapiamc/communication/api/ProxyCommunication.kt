package net.tilapiamc.communication.api

import io.ktor.client.*
import io.ktor.websocket.*
import net.tilapiamc.common.SuspendEventTarget
import net.tilapiamc.communication.DatabaseLogin
import net.tilapiamc.communication.session.PacketRegistry
import net.tilapiamc.communication.session.Session
import net.tilapiamc.communication.session.SessionEvent
import java.util.*

class ProxyCommunication(client: HttpClient): TilapiaPrivateAPI(client) {

    suspend fun start(requiredSchemas: List<String>,
              eventTargetFactory: (ignoreException: Boolean) -> SuspendEventTarget<out SessionEvent>
    ): ProxyCommunicationSession {
        TODO()
    }

}

class ProxyCommunicationSession(requiredSchemas: List<String>,
                                val communication: ProxyCommunication,
                                eventTargetFactory: (ignoreException: Boolean) -> SuspendEventTarget<out SessionEvent>,
                                websocketSession: WebSocketSession
): Session(PacketRegistry.serverPackets, eventTargetFactory, websocketSession) {

    val onProxyConnected = eventTargetFactory(false) as SuspendEventTarget<ProxyConnectedEvent>

    lateinit var proxyId: UUID
    lateinit var databaseLogin: DatabaseLogin

    init {
        onSessionStarted.add {
//            val handShake = waitForPacketWithType<SPacketProxyHandShake>()?:clientError("Not receiving handshake packet")
//            proxyId = handShake.proxyId
//            sendPacket(CPacketProxyHandShake(requiredSchemas))
//            val databasePacket = waitForPacketWithType<SPacketDatabaseLogin>()?:clientError("Not receiving database login packet")
//            databaseLogin = databasePacket.databaseLogin
//            onProxyConnected(ProxyConnectedEvent(this))
        }
    }

    fun login(player: UUID) {
        TODO()

    }
    fun logout(player: UUID) {
        TODO()

    }

    class ProxyConnectedEvent(override val session: ProxyCommunicationSession): SessionEvent(session)

}