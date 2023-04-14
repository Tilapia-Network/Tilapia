package net.tiapiamc.session

import io.ktor.websocket.*
import net.tiapiamc.getSuspendEventTarget
import net.tilapiamc.communication.session.PacketRegistry
import net.tilapiamc.communication.session.Session
import net.tilapiamc.communication.session.client.CPacketServerHandShake
import net.tilapiamc.communication.session.server.SPacketServerHandShake
import java.util.*

class ServerSession(webSocket: WebSocketSession,
                    val proxyId: UUID,
                    val serverId: UUID
) : Session(PacketRegistry.serverPackets, { getSuspendEventTarget(it) }, webSocket) {

    init {
        onSessionStarted.add {
            sendPacket(SPacketServerHandShake(proxyId, serverId))
            val handShake = waitForPacketWithType<CPacketServerHandShake>()?:clientError("Not receiving handshake packet")
        }
    }

}