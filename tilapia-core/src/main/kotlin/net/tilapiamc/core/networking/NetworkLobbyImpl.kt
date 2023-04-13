package net.tilapiamc.core.networking

import net.tilapiamc.api.game.lobby.Lobby
import net.tilapiamc.api.game.minigame.MiniGame
import net.tilapiamc.communication.LobbyInfo
import net.tilapiamc.communication.MiniGameInfo
import net.tilapiamc.communication.api.ServerCommunication
import java.util.UUID

class NetworkLobbyImpl(communication: ServerCommunication,
                       data: LobbyInfo
    ): Lobby(NetworkServerImpl.getServer(communication, data.serverId)!!, data.gameId, false, data.lobbyType) {



}