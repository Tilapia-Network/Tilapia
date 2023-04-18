package net.tilapiamc.proxyapi.servers

import net.tilapiamc.proxyapi.TilapiaProxyAPI
import org.slf4j.LoggerFactory
import java.util.*

class LocalServerManager(val proxyAPI: TilapiaProxyAPI) {

    val servers = HashMap<UUID, LocalTilapiaServer>()
    val logger = LoggerFactory.getLogger("LocalServerManager")

    fun register(tilapiaServer: TilapiaServer): LocalTilapiaServer {
        if (tilapiaServer.serverId !in servers) {
            logger.info("Registered server: ${tilapiaServer.serverId}  (To ${tilapiaServer.address})")
            val server = proxyAPI.internal.createLocalServer(tilapiaServer)
            servers[tilapiaServer.serverId] = server
            proxyAPI.proxy.registerServer(server.toServerInfo())
        }
        return servers[tilapiaServer.serverId]!!
    }

    fun unregister(tilapiaServer: LocalTilapiaServer) {
        if (tilapiaServer.serverId in servers) {
            logger.info("Removed server: ${tilapiaServer.serverId}")
            servers.remove(tilapiaServer.serverId)
            proxyAPI.proxy.unregisterServer(tilapiaServer.toServerInfo())
        }
    }

}