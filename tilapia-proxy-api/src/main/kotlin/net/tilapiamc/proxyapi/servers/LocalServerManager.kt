package net.tilapiamc.proxyapi.servers

import com.velocitypowered.api.proxy.server.ServerInfo
import net.tilapiamc.proxyapi.TilapiaProxyAPI
import java.util.*

class LocalServerManager(val proxyAPI: TilapiaProxyAPI) {

    val servers = HashMap<UUID, LocalTilapiaServer>()

    fun register(tilapiaServer: TilapiaServer): LocalTilapiaServer {
        if (tilapiaServer.serverId !in servers) {
            val server = proxyAPI.internal.createLocalServer(tilapiaServer)
            servers[tilapiaServer.serverId] = server
            proxyAPI.proxy.registerServer(ServerInfo(tilapiaServer.serverId.toString(), tilapiaServer.address))
        }
        return servers[tilapiaServer.serverId]!!
    }

}