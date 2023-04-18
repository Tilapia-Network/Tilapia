package net.tilapiamc.proxycore

import net.tilapiamc.proxyapi.servers.LocalTilapiaServer
import java.net.InetSocketAddress
import java.util.*

class LocalServerImpl(address: InetSocketAddress, proxyId: UUID, serverId: UUID) : LocalTilapiaServer(address, proxyId,
    serverId
) {
}