package net.tilapiamc.api.game.minigame

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import net.tilapiamc.api.TilapiaCore
import net.tilapiamc.api.events.EventsManager
import net.tilapiamc.api.events.game.PlayerJoinGameEvent
import net.tilapiamc.api.events.game.PlayerJoinMiniGameEvent
import net.tilapiamc.api.events.game.SpectatorJoinEvent
import net.tilapiamc.api.game.GameType
import net.tilapiamc.api.game.ManagedGame
import net.tilapiamc.api.player.LocalNetworkPlayer
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bukkit.World
import java.util.*

abstract class ManagedMiniGame(
    val core: TilapiaCore,
    final override val gameWorld: World,
    lobbyType: String,
    miniGameType: String
): MiniGame(core.getLocalServer(), core.provideGameId(GameType.MINIGAME), true, lobbyType, miniGameType), ManagedGame {


    final override val logger: Logger = LogManager.getLogger("MiniGame $gameId")
    override val inGamePlayers = ArrayList<LocalNetworkPlayer>()
    val spectatorPlayers = ArrayList<LocalNetworkPlayer>()

    init {
        logger.info("Assigned world: ${gameWorld.name} to mini game $shortGameId")
    }

    override fun getManagedGameId(): UUID {
        return gameId
    }


    override fun start() {
        onStart()
    }

    override fun end() {
        onEnd()
        core.removeGame(this)
    }

    fun addSpectator(networkPlayer: LocalNetworkPlayer) {
        if (networkPlayer !in this.spectatorPlayers) {
            this.inGamePlayers.remove(networkPlayer)
            this.spectatorPlayers.add(networkPlayer)
            EventsManager.fireEvent(PlayerJoinGameEvent(this, networkPlayer))
            EventsManager.fireEvent(SpectatorJoinEvent(this, networkPlayer))

            if (networkPlayer !in this.players) {
                super.add(networkPlayer)
            }
        }


    }
    override fun add(networkPlayer: LocalNetworkPlayer) {
        if (networkPlayer !in this.inGamePlayers) {
            this.inGamePlayers.add(networkPlayer)
            this.spectatorPlayers.remove(networkPlayer)
            EventsManager.fireEvent(PlayerJoinMiniGameEvent(this, networkPlayer))
            if (networkPlayer !in this.players) {
                super.add(networkPlayer)
            }
        }
    }

    override fun remove(networkPlayer: LocalNetworkPlayer) {
        this.inGamePlayers.remove(networkPlayer)
        this.spectatorPlayers.remove(networkPlayer)
        super.remove(networkPlayer)
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