package net.tilapiamc.core.networking

import net.tilapiamc.api.game.lobby.Lobby
import net.tilapiamc.communication.LobbyInfo
import net.tilapiamc.communication.api.ServerCommunication

class NetworkLobbyImpl(communication: ServerCommunication,
                       data: LobbyInfo
    ): Lobby(NetworkServerImpl.getServer(communication, data.serverId)!!, data.gameId, false, data.lobbyType) {



}