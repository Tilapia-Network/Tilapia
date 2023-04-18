package net.tiapiamc.managers

import io.ktor.websocket.*
import net.tiapiamc.obj.Player
import net.tiapiamc.obj.game.Game
import net.tiapiamc.session.ProxySession
import net.tiapiamc.session.ServerSession
import net.tilapiamc.communication.session.server.proxy.SPacketProxyAddServer
import net.tilapiamc.communication.session.server.proxy.SPacketProxyRemoveServer
import org.slf4j.LoggerFactory
import java.util.*

class ServerManager {

    val servers = HashMap<UUID, ServerSession>()
    val proxies = HashMap<UUID,  ProxySession>()
    val games = HashMap<UUID, Game>()
    val players = HashMap<UUID, Player>()


    fun sendToGame(player: Player, game: Game) {
        player.currentGame = game
    }

    fun generateProxyId(): UUID = UUID.randomUUID()
    fun generateServerId(): UUID = UUID.randomUUID()
    fun getProxyAssignment(): ProxySession = proxies.values.random()

    val logger = LoggerFactory.getLogger("ServerManager")

    suspend fun createProxy(proxy: ProxySession) {
        logger.info("Proxy ${proxy.proxyId} has started")
        proxies[proxy.proxyId] = proxy
    }
    suspend fun createServer(server: ServerSession) {
        logger.info("Server ${server.serverId} has started")
        servers[server.serverId] = server
        server.proxy.servers.add(server)
        server.proxy.sendPacket(SPacketProxyAddServer(server.toServerInfo()))
    }
    suspend fun deleteProxy(proxy: ProxySession, closedByServer: Boolean, reason: CloseReason?) {
        if (proxy.proxyId in proxies) {
            logger.info("Proxy ${proxy.proxyId} has stopped, closing ${proxy.servers.size} servers, reason: ${reason?.message}")
        } else {
            logger.info("Proxy ${proxy.proxyId} has disconnected before handshake, reason: ${reason?.message}")
        }
        proxies.remove(proxy.proxyId)
        for (server in ArrayList(proxy.servers)) {
            server.closeSession(CloseReason(CloseReason.Codes.NORMAL, "The parent proxy has stopped"))
        }
    }
    suspend fun deleteServer(server: ServerSession, closedByServer: Boolean, reason: CloseReason?) {
        if (server.serverId in servers) {
            logger.info("Server ${server.serverId} has stopped, reason: ${reason?.message}")
        } else {
            logger.info("Server ${server.serverId} has disconnected before handshake, reason: ${reason?.message}")
        }
        for (game in server.games) {
            games.remove(game.gameId)
        }
        servers.remove(server.serverId)
        server.proxy.servers.remove(server)
        server.proxy.sendPacket(SPacketProxyRemoveServer(server.serverId))
    }

}