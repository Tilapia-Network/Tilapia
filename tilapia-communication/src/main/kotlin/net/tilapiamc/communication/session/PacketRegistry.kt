package net.tilapiamc.communication.session

import net.tilapiamc.communication.session.client.CPacketProxyHandShake
import net.tilapiamc.communication.session.client.CPacketServerHandShake
import net.tilapiamc.communication.session.server.SPacketDatabaseLogin
import net.tilapiamc.communication.session.server.SPacketProxyHandShake
import net.tilapiamc.communication.session.server.SPacketServerHandShake

object PacketRegistry {

    val serverPackets = hashMapOf<String, () -> SessionPacket>(
        "SPacketServerHandShake" to { SPacketServerHandShake() },
        "SPacketProxyHandShake" to { SPacketProxyHandShake() },
        "SPacketDatabaseLogin" to { SPacketDatabaseLogin() },

    )
    val clientPackets = hashMapOf<String, () -> SessionPacket>(
        "CPacketServerHandShake" to { CPacketServerHandShake() },
        "CPacketProxyHandShake" to { CPacketProxyHandShake() },
    )

    fun getPacketName(packet: SessionPacket): String {
        return serverPackets.entries.firstOrNull { it.value().javaClass.isAssignableFrom(packet.javaClass) }?.key?:
                clientPackets.entries.firstOrNull { it.value().javaClass.isAssignableFrom(packet.javaClass) }?.key?: throw IllegalArgumentException("Invalid packet, the packet is not registered")
    }

}