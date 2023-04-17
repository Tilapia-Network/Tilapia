package net.tilapiamc.proxyapi.servers

import java.net.InetSocketAddress
import java.util.*

abstract class LocalTilapiaServer(address: InetSocketAddress, proxyId: UUID, serverId: UUID) : TilapiaServer(address, proxyId, serverId) {
}