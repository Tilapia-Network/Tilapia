package net.tilapiamc.proxycore.networking

import net.tilapiamc.communication.MiniGameInfo
import net.tilapiamc.communication.api.ProxyCommunicationSession
import net.tilapiamc.proxyapi.game.MiniGame


class NetworkMiniGameImpl(session: ProxyCommunicationSession,
                          data: MiniGameInfo,
): MiniGame(NetworkServerImpl.getServer(session.communication, data.serverId)!!,
    data.gameId,
    false,
    data.lobbyType,
    data.miniGameType,
    data.properties) {

    init {
        data.players.forEach { players.add(NetworkPlayerImpl(session, it)) }
        data.spectators.forEach { players.add(NetworkPlayerImpl(session, it)) }
    }

}