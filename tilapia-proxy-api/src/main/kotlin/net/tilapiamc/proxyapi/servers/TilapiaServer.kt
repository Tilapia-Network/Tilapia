package net.tilapiamc.proxyapi.servers

import java.net.InetSocketAddress
import java.util.*

abstract class TilapiaServer(val address: InetSocketAddress, val proxyId: UUID, val serverId: UUID) {
}