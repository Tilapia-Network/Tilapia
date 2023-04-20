package net.tilapiamc.api.game.lobby

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import net.tilapiamc.api.TilapiaCore
import net.tilapiamc.api.game.GameType
import net.tilapiamc.api.game.ManagedGame
import net.tilapiamc.api.player.LocalNetworkPlayer
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bukkit.World
import java.util.*

abstract class ManagedLobby(
    val core: TilapiaCore,
    final override val gameWorld: World,
    lobbyType: String
): Lobby(core.getLocalServer(), core.provideGameId(GameType.LOBBY), true, lobbyType), ManagedGame {
    final override val logger: Logger = LogManager.getLogger("Lobby $gameId")

    val localPlayers: List<LocalNetworkPlayer>
        get() = super.players.filterIsInstance<LocalNetworkPlayer>()
    init {
        logger.info("Assigned world: ${gameWorld.name} to lobby $shortGameId")
    }

    override fun end() {
        onEnd()
        core.removeGame(this)
    }

    override fun getManagedGameId(): UUID {
        return gameId
    }

    override fun start() {
        onStart()
    }

    open val gson = GsonBuilder().create()
    private val properties = JsonObject()
    override fun hasProperty(name: String): Boolean {
        return properties.has(name)
    }

    override fun getProperty(name: String): JsonElement? {
        return properties[name]
    }

    override fun setProperty(name: String, value: Any) {
        properties.add(name, gson.toJsonTree(value))
        core.updateGame(this)
    }

    override fun removeProperty(name: String) {
        properties.remove(name)
        core.updateGame(this)
    }
    override fun getProperties(): Map<String, JsonElement> {
        return HashMap<String, JsonElement>().also {
            for (mutableEntry in properties.entrySet()) {
                it[mutableEntry.key] = mutableEntry.value
            }
        }
    }
}