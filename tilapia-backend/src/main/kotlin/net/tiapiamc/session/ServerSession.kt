package net.tiapiamc.session

import io.ktor.websocket.*
import net.tiapiamc.data.DatabaseManager
import net.tiapiamc.getSuspendEventTarget
import net.tilapiamc.communication.session.PacketRegistry
import net.tilapiamc.communication.session.Session
import net.tilapiamc.communication.session.client.CPacketServerHandShake
import net.tilapiamc.communication.session.server.SPacketDatabaseLogin
import net.tilapiamc.communication.session.server.SPacketServerHandShake
import java.util.*

class ServerSession(val remoteIp: String,
                    webSocket: WebSocketSession,
                    val proxyId: UUID,
                    val serverId: UUID
) : Session(PacketRegistry.clientPackets, { getSuspendEventTarget(it) }, webSocket) {

    init {
        onSessionStarted.add {
            sendPacket(SPacketServerHandShake(proxyId, serverId))
            val handShake = waitForPacketWithType<CPacketServerHandShake>()?:clientError("Not receiving handshake packet")
            val login = DatabaseManager.createSession(remoteIp, handShake.requiredSchemas)
            onSessionClosed.add {
                DatabaseManager.closeSession(login.sessionId)
            }
            sendPacket(SPacketDatabaseLogin(login))
        }
    }

}