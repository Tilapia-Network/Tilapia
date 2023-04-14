package net.tiapiamc.session

import io.ktor.websocket.*
import net.tiapiamc.getSuspendEventTarget
import net.tilapiamc.communication.session.PacketRegistry
import net.tilapiamc.communication.session.Session
import java.util.*

class ProxySession(webSocket: WebSocketSession,
                    val proxyId: UUID
) : Session(PacketRegistry.clientPackets, { getSuspendEventTarget(it) }, webSocket) {


}