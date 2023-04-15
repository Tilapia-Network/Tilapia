package net.tilapiamc.communication.session

import net.tilapiamc.communication.session.client.CPacketAcknowledge
import net.tilapiamc.communication.session.client.CPacketStatus
import net.tilapiamc.communication.session.client.proxy.CPacketProxyHandShake
import net.tilapiamc.communication.session.client.proxy.CPacketProxyPlayerLogin
import net.tilapiamc.communication.session.client.proxy.CPacketProxyPlayerLogout
import net.tilapiamc.communication.session.client.server.CPacketServerHandShake
import net.tilapiamc.communication.session.server.SPacketAcknowledge
import net.tilapiamc.communication.session.server.SPacketDatabaseLogin
import net.tilapiamc.communication.session.server.SPacketStatus
import net.tilapiamc.communication.session.server.proxy.SPacketProxyAcceptPlayer
import net.tilapiamc.communication.session.server.proxy.SPacketProxyAddServer
import net.tilapiamc.communication.session.server.proxy.SPacketProxyHandShake
import net.tilapiamc.communication.session.server.proxy.SPacketProxyRemoveServer
import net.tilapiamc.communication.session.server.server.SPacketServerAcceptPlayer
import net.tilapiamc.communication.session.server.server.SPacketServerHandShake

object PacketRegistry {

    val serverPackets = hashMapOf<String, () -> SessionPacket>(
        "SPacketServerHandShake" to { SPacketServerHandShake() },
        "SPacketProxyHandShake" to { SPacketProxyHandShake() },
        "SPacketDatabaseLogin" to { SPacketDatabaseLogin() },
        "SPacketProxyAddServer" to { SPacketProxyAddServer() },
        "SPacketProxyRemoveServer" to { SPacketProxyRemoveServer() },
        "SPacketAcknowledge" to { SPacketAcknowledge() },
        "SPacketServerAcceptPlayer" to { SPacketServerAcceptPlayer() },
        "SPacketProxyAcceptPlayer" to { SPacketProxyAcceptPlayer() },
        "SPacketStatus" to { SPacketStatus() },
    )
    val clientPackets = hashMapOf<String, () -> SessionPacket>(
        "CPacketServerHandShake" to { CPacketServerHandShake() },
        "CPacketProxyHandShake" to { CPacketProxyHandShake() },
        "CPacketAcknowledge" to { CPacketAcknowledge() },
        "CPacketStatus" to { CPacketStatus() },
        "CPacketProxyPlayerLogin" to { CPacketProxyPlayerLogin() },
        "CPacketProxyPlayerLogout" to { CPacketProxyPlayerLogout() },
    )

    fun getPacketName(packet: SessionPacket): String {
        return serverPackets.entries.firstOrNull { it.value().javaClass.isAssignableFrom(packet.javaClass) }?.key?:
                clientPackets.entries.firstOrNull { it.value().javaClass.isAssignableFrom(packet.javaClass) }?.key?: throw IllegalArgumentException("Invalid packet, the packet is not registered")
    }

}