package net.tiapiamc.session

import io.ktor.websocket.*
import net.tiapiamc.data.DatabaseManager
import net.tiapiamc.getSuspendEventTarget
import net.tiapiamc.obj.Player
import net.tiapiamc.obj.game.Game
import net.tilapiamc.common.SuspendEventTarget
import net.tilapiamc.communication.ServerInfo
import net.tilapiamc.communication.session.PacketRegistry
import net.tilapiamc.communication.session.Session
import net.tilapiamc.communication.session.SessionEvent
import net.tilapiamc.communication.session.client.server.CPacketServerHandShake
import net.tilapiamc.communication.session.server.SPacketDatabaseLogin
import net.tilapiamc.communication.session.server.server.SPacketServerHandShake
import java.util.*

class ServerSession(val remoteIp: String,
                    webSocket: DefaultWebSocketSession,
                    val proxy: ProxySession,
                    val serverId: UUID
) : Session(PacketRegistry.clientPackets, { getSuspendEventTarget(it) }, webSocket) {

    val games = ArrayList<Game>()
    val players = HashMap<UUID, Player>()
    val onHandshakeFinished = eventTargetFactory(false) as SuspendEventTarget<HandshakeFinishedEvent>

    init {
        onSessionStarted.add {
            sendPacket(SPacketServerHandShake(proxy.proxyId, serverId))
            val handShake = waitForPacketWithType<CPacketServerHandShake>()?:clientError("Not receiving handshake packet")
            val login = DatabaseManager.createSession(remoteIp, handShake.requiredSchemas)
            onSessionClosed.add {
                DatabaseManager.closeSession(login.sessionId)
            }
            sendPacket(SPacketDatabaseLogin(login))
            onHandshakeFinished(HandshakeFinishedEvent(this))
        }
    }

    fun toServerInfo(): ServerInfo = ServerInfo(proxy.proxyId, serverId, games.map { it.gameId })

    class HandshakeFinishedEvent(override val session: ServerSession): SessionEvent(session)

}