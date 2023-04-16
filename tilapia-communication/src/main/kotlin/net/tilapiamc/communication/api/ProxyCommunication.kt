package net.tilapiamc.communication.api

import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import net.tilapiamc.common.SuspendEventTarget
import net.tilapiamc.communication.DatabaseLogin
import net.tilapiamc.communication.PlayerInfo
import net.tilapiamc.communication.ServerInfo
import net.tilapiamc.communication.session.PacketRegistry
import net.tilapiamc.communication.session.Session
import net.tilapiamc.communication.session.SessionEvent
import net.tilapiamc.communication.session.client.CPacketAcknowledge
import net.tilapiamc.communication.session.client.proxy.CPacketProxyHandShake
import net.tilapiamc.communication.session.client.proxy.CPacketProxyPlayerLogin
import net.tilapiamc.communication.session.client.proxy.CPacketProxyPlayerLogout
import net.tilapiamc.communication.session.server.SPacketDatabaseLogin
import net.tilapiamc.communication.session.server.proxy.SPacketProxyAcceptPlayer
import net.tilapiamc.communication.session.server.proxy.SPacketProxyAddServer
import net.tilapiamc.communication.session.server.proxy.SPacketProxyHandShake
import net.tilapiamc.communication.session.server.proxy.SPacketProxyRemoveServer
import java.util.*

class ProxyCommunication(client: HttpClient): TilapiaPrivateAPI(client) {

    suspend fun start(requiredSchemas: List<String>,
              eventTargetFactory: (ignoreException: Boolean) -> SuspendEventTarget<out SessionEvent>,
              block: suspend ProxyCommunicationSession.() -> Unit = {}
    ): CloseReason? {
        var reason: CloseReason? = null
        client.webSocket("/proxy/gateway") {
            ProxyCommunicationSession(requiredSchemas, this@ProxyCommunication, eventTargetFactory, this).also {
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

class ProxyCommunicationSession(requiredSchemas: List<String>,
                                val communication: ProxyCommunication,
                                eventTargetFactory: (ignoreException: Boolean) -> SuspendEventTarget<out SessionEvent>,
                                websocketSession: DefaultWebSocketSession
): Session(PacketRegistry.serverPackets, eventTargetFactory, websocketSession) {

    val onProxyConnected = eventTargetFactory(false) as SuspendEventTarget<ProxyConnectedEvent>
    val onServerAdded = eventTargetFactory(false) as SuspendEventTarget<ProxyAddServerEvent>
    val onServerRemoved = eventTargetFactory(false) as SuspendEventTarget<ProxyRemoveServerEvent>
    val onPlayerAccepted = eventTargetFactory(false) as SuspendEventTarget<SPacketProxyAcceptPlayer>

    lateinit var proxyId: UUID
    lateinit var databaseLogin: DatabaseLogin

    init {
        onSessionStarted.add {
            val handShake = waitForPacketWithType<SPacketProxyHandShake>()?:clientError("Not receiving handshake packet")
            proxyId = handShake.proxyId
            sendPacket(CPacketProxyHandShake(requiredSchemas))
            val databasePacket = waitForPacketWithType<SPacketDatabaseLogin>()?:clientError("Not receiving database login packet")
            databaseLogin = databasePacket.databaseLogin
            onProxyConnected(ProxyConnectedEvent(this))
        }

        onPacket.add {
            if (it.packet is SPacketProxyAddServer) {
                onServerAdded(ProxyAddServerEvent(this, it.packet.server))
            }
            if (it.packet is SPacketProxyRemoveServer) {
                onServerRemoved(ProxyRemoveServerEvent(this, it.packet.serverId))
            }
            if (it.packet is SPacketProxyAcceptPlayer) {
                onPlayerAccepted(it.packet)
                sendPacket(CPacketAcknowledge(it.packet.transmissionId))
            }
        }
    }

    suspend fun login(player: PlayerInfo) {
        sendPacket(CPacketProxyPlayerLogin(player))
    }
    suspend fun logout(player: UUID) {
        sendPacket(CPacketProxyPlayerLogout(player))
    }

    class ProxyConnectedEvent(override val session: ProxyCommunicationSession): SessionEvent(session)
    class ProxyAddServerEvent(override val session: ProxyCommunicationSession, val serverInfo: ServerInfo): SessionEvent(session)
    class ProxyRemoveServerEvent(override val session: ProxyCommunicationSession, val serverId: UUID): SessionEvent(session)

}