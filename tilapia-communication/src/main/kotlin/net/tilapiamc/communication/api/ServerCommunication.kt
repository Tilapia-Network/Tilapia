package net.tilapiamc.communication.api

import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import net.tilapiamc.common.SuspendEventTarget
import net.tilapiamc.communication.DatabaseLogin
import net.tilapiamc.communication.JoinResult
import net.tilapiamc.communication.PlayerInfo
import net.tilapiamc.communication.session.PacketRegistry
import net.tilapiamc.communication.session.Session
import net.tilapiamc.communication.session.SessionEvent
import net.tilapiamc.communication.session.client.CPacketAcknowledge
import net.tilapiamc.communication.session.client.server.CPacketServerHandShake
import net.tilapiamc.communication.session.client.server.CPacketServerJoinResult
import net.tilapiamc.communication.session.server.SPacketDatabaseLogin
import net.tilapiamc.communication.session.server.server.SPacketServerAcceptPlayer
import net.tilapiamc.communication.session.server.server.SPacketServerHandShake
import net.tilapiamc.communication.session.server.server.SPacketServerRequestJoinResult
import java.util.*

class ServerCommunication(client: HttpClient): TilapiaPrivateAPI(client) {

    suspend fun start(
              requiredSchemas: List<String>,
              eventTargetFactory: (ignoreException: Boolean) -> SuspendEventTarget<out SessionEvent>,
              getPlayerJoinResult: (PlayerInfo, UUID, Boolean) -> JoinResult = { _, _, _ -> JoinResult(true, 1.0, "") },
              port: Int = 25565,
              block: suspend ServerCommunicationSession.() -> Unit = {},

    ): CloseReason? {
        var reason: CloseReason? = null
        client.webSocket("/server/gateway?port=$port") {
            ServerCommunicationSession(requiredSchemas, this@ServerCommunication, eventTargetFactory, this, getPlayerJoinResult).also {
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
                                websocketSession: DefaultWebSocketSession,
                                val getPlayerJoinResult: (playerInfo: PlayerInfo, gameId: UUID, forceJoin: Boolean) -> JoinResult
): Session(PacketRegistry.serverPackets, eventTargetFactory, websocketSession) {

    val onServerConnected = eventTargetFactory(false) as SuspendEventTarget<ServerConnectedEvent>
    val onPlayerAccepted = eventTargetFactory(false) as SuspendEventTarget<SPacketServerAcceptPlayer>

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
        onPacket.add {
            if (it.packet is SPacketServerRequestJoinResult) {
                Thread.sleep(10)
                sendPacket(CPacketServerJoinResult(it.packet.transmissionId, getPlayerJoinResult(it.packet.player, it.packet.gameId, it.packet.forceJoin)))
            }
            if (it.packet is SPacketServerAcceptPlayer) {
                onPlayerAccepted(it.packet)
                sendPacket(CPacketAcknowledge(it.packet.transmissionId))
            }
        }
    }


    class ServerConnectedEvent(override val session: ServerCommunicationSession): SessionEvent(session)

}