package net.tiapiamc.session

import io.ktor.websocket.*
import net.tiapiamc.getSuspendEventTarget
import net.tilapiamc.communication.session.PacketRegistry
import net.tilapiamc.communication.session.Session
import java.util.*

class ProxySession(val remoteIp: String,
                   webSocket: WebSocketSession,
                   val proxyId: UUID
) : Session(PacketRegistry.clientPackets, { getSuspendEventTarget(it) }, webSocket) {

    init {
        onSessionStarted.add {
//            sendPacket(SPacketProxyHandShake(proxyId))
//            val handShake = waitForPacketWithType<CPacketProxyHandShake>()?:clientError("Not receiving handshake packet")
//            DatabaseManager.createSession(remoteIp, handShake.requiredSchemas)
        }
    }

}