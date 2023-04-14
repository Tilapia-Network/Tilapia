package net.tilapiamc.communication.session

import net.tilapiamc.communication.session.client.CPacketProxyHandShake
import net.tilapiamc.communication.session.client.CPacketServerHandShake
import net.tilapiamc.communication.session.server.SPacketProxyHandShake
import net.tilapiamc.communication.session.server.SPacketServerHandShake

object PacketRegistry {

    val serverPackets = hashMapOf<String, () -> SessionPacket>(
        "SPacketServerHandShake" to { SPacketServerHandShake() },
        "SPacketProxyHandShake" to { SPacketProxyHandShake() },

    )
    val clientPackets = hashMapOf<String, () -> SessionPacket>(
        "CPacketServerHandShake" to { CPacketServerHandShake() },
        "CPacketProxyHandShake" to { CPacketProxyHandShake() },
    )

}