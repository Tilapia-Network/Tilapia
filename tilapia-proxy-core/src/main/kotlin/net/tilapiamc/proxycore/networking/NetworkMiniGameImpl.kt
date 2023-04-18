package net.tilapiamc.proxycore.networking

import net.tilapiamc.communication.MiniGameInfo
import net.tilapiamc.communication.api.ProxyCommunication
import net.tilapiamc.proxyapi.game.MiniGame


class NetworkMiniGameImpl(communication: ProxyCommunication,
                          data: MiniGameInfo,
): MiniGame(NetworkServerImpl.getServer(communication, data.serverId)!!, data.gameId, false, data.lobbyType, data.miniGameType) {



}