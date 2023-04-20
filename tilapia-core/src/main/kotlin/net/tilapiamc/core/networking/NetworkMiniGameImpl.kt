package net.tilapiamc.core.networking

import com.google.gson.JsonElement
import net.tilapiamc.api.game.minigame.MiniGame
import net.tilapiamc.communication.MiniGameInfo
import net.tilapiamc.communication.api.ServerCommunication


class NetworkMiniGameImpl(communication: ServerCommunication,
                       data: MiniGameInfo,
): MiniGame(NetworkServerImpl.getServer(communication, data.serverId)!!, data.gameId, false, data.lobbyType, data.miniGameType) {

    val properties = data.properties

    override fun getProperty(name: String): JsonElement? {
        return properties.get(name)
    }

    override fun removeProperty(name: String) {
        throw UnsupportedOperationException("Remove property is not supported on remote game")
    }

    override fun setProperty(name: String, value: Any) {
        throw UnsupportedOperationException("Set property is not supported on remote game")
    }

    override fun getProperties(): Map<String, JsonElement> {
        return HashMap<String, JsonElement>().also {
            for (mutableEntry in properties.entrySet()) {
                it[mutableEntry.key] = mutableEntry.value
            }
        }
    }


}