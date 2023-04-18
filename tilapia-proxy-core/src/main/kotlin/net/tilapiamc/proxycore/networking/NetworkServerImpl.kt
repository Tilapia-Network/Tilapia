package net.tilapiamc.proxycore.networking

import net.tilapiamc.communication.ServerInfo
import net.tilapiamc.communication.api.ProxyCommunication
import net.tilapiamc.proxyapi.servers.TilapiaServer
import java.net.InetSocketAddress
import java.util.*

class NetworkServerImpl(val data: ServerInfo): TilapiaServer(InetSocketAddress(data.host, data.port), data.proxy, data.serverId) {

    companion object {
        val cache = HashMap<UUID, TilapiaServer>()
        fun getServer(communication: ProxyCommunication, uuid: UUID): TilapiaServer? {
            if (uuid in cache) {
                return cache[uuid]!!
            }
            // TODO: GetServerInfo
            val data = ServerInfo("localhost", 25565, UUID.randomUUID(), uuid, emptyList())?:return null
            val server = NetworkServerImpl(data)
            cache[uuid] = server
            return server
        }
    }

}