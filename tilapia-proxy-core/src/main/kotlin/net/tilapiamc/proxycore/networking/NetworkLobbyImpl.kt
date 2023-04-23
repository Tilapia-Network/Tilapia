package net.tilapiamc.proxycore.networking

import net.tilapiamc.communication.LobbyInfo
import net.tilapiamc.communication.api.ProxyCommunicationSession
import net.tilapiamc.proxyapi.game.Lobby

class NetworkLobbyImpl(session: ProxyCommunicationSession,
                       data: LobbyInfo
    ): Lobby(NetworkServerImpl.getServer(session.communication, data.serverId)!!, data.gameId, false, data.lobbyType, data.properties) {

    init {
        data.players.forEach { players.add(NetworkPlayerImpl(session, it)) }
    }

}