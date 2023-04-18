package net.tilapiamc.proxyapi.servers

import com.velocitypowered.api.proxy.server.ServerInfo
import java.net.InetSocketAddress
import java.util.*

abstract class LocalTilapiaServer(address: InetSocketAddress, proxyId: UUID, serverId: UUID) : TilapiaServer(address, proxyId, serverId) {

    fun toServerInfo(): ServerInfo = ServerInfo(serverId.toString(), address)

}