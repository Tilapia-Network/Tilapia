package net.tiapiamc.session

import io.ktor.websocket.*
import net.tiapiamc.data.DatabaseManager
import net.tiapiamc.getSuspendEventTarget
import net.tiapiamc.obj.Player
import net.tilapiamc.communication.ProxyInfo
import net.tilapiamc.communication.session.PacketRegistry
import net.tilapiamc.communication.session.Session
import net.tilapiamc.communication.session.client.proxy.CPacketProxyHandShake
import net.tilapiamc.communication.session.server.SPacketDatabaseLogin
import net.tilapiamc.communication.session.server.proxy.SPacketProxyHandShake
import java.util.*

class ProxySession(val remoteIp: String,
                   webSocket: DefaultWebSocketSession,
                   val proxyId: UUID
) : Session(PacketRegistry.clientPackets, { getSuspendEventTarget(it) }, webSocket) {

    val players = HashMap<UUID, Player>()
    val servers = ArrayList<ServerSession>()

    init {
        onSessionStarted.add {
            sendPacket(SPacketProxyHandShake(proxyId))
            val handShake = waitForPacketWithType<CPacketProxyHandShake>()?:clientError("Not receiving handshake packet")
            val login = DatabaseManager.createSession(remoteIp, handShake.requiredSchemas)
            onSessionClosed.add {
                DatabaseManager.closeSession(login.sessionId)
            }
            sendPacket(SPacketDatabaseLogin(login))
        }
    }

    fun toProxyInfo(): ProxyInfo {
        return ProxyInfo(proxyId, servers.map { it.toServerInfo() })
    }

}