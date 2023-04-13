package net.tilapiamc.core.networking

import net.tilapiamc.api.game.minigame.MiniGame
import net.tilapiamc.communication.MiniGameInfo
import net.tilapiamc.communication.api.ServerCommunication


class NetworkMiniGameImpl(communication: ServerCommunication,
                       data: MiniGameInfo,
): MiniGame(NetworkServerImpl.getServer(communication, data.serverId)!!, data.gameId, false, data.lobbyType, data.miniGameType) {



}