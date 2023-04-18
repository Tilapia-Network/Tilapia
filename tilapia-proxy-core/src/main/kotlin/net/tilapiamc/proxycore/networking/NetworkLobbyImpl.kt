package net.tilapiamc.proxycore.networking

import net.tilapiamc.communication.LobbyInfo
import net.tilapiamc.communication.api.ProxyCommunication
import net.tilapiamc.proxyapi.game.Lobby

class NetworkLobbyImpl(communication: ProxyCommunication,
                       data: LobbyInfo
    ): Lobby(NetworkServerImpl.getServer(communication, data.serverId)!!, data.gameId, false, data.lobbyType) {



}