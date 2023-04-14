package net.tilapiamc.core.networking

import net.tilapiamc.api.server.TilapiaServer
import net.tilapiamc.communication.ServerInfo
import net.tilapiamc.communication.api.ServerCommunication
import java.util.*

class NetworkServerImpl(val data: ServerInfo): TilapiaServer(data.proxy, data.serverId) {

    companion object {
        val cache = HashMap<UUID, TilapiaServer>()
        fun getServer(communication: ServerCommunication, uuid: UUID): TilapiaServer? {
            if (uuid in cache) {
                return cache[uuid]!!
            }
            val data = communication.getServerInfo(uuid)?:return null
            val server = NetworkServerImpl(data)
            cache[uuid] = server
            return server
        }
    }

}