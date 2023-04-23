package net.tilapiamc.api.game

import net.tilapiamc.api.TilapiaCore
import net.tilapiamc.api.events.EventsManager
import net.tilapiamc.api.events.game.PlayerCheckAddEvent
import net.tilapiamc.api.events.game.PlayerJoinGameEvent
import net.tilapiamc.api.events.game.PlayerQuitGameEvent
import net.tilapiamc.api.game.lobby.Lobby
import net.tilapiamc.api.player.LocalNetworkPlayer
import net.tilapiamc.api.player.NetworkPlayer
import org.apache.logging.log4j.Logger
import org.bukkit.Location
import org.bukkit.OfflinePlayer
import org.bukkit.World
import java.util.*
import javax.persistence.Lob

interface ManagedGame: IGame {



    val logger: Logger

    val gameWorld: World
    fun onStart()
    fun onEnd()
    fun end()
    fun start()

    fun getManagedGameId(): UUID


    fun couldAdd(networkPlayer: NetworkPlayer, forceJoin: Boolean): PlayerJoinResult {
        val event = PlayerCheckAddEvent(this, networkPlayer, couldAddPlayer(networkPlayer, forceJoin))
        EventsManager.fireEvent(event)
        if (TilapiaCore.instance.shuttingDown && event.result.type.success) {
            return PlayerJoinResult(PlayerJoinResultType.DENIED, 0.0, "Server is shutting down")
        }
        return event.result
    }
    fun couldAddPlayer(networkPlayer: NetworkPlayer, forceJoin: Boolean): PlayerJoinResult
    fun add(networkPlayer: LocalNetworkPlayer) {
        (this as Game).players.add(networkPlayer)
        val event = PlayerJoinGameEvent(this, networkPlayer)
        EventsManager.fireEvent(event)
        addPlayer(networkPlayer)
    }
    fun remove(networkPlayer: LocalNetworkPlayer) {
        (this as Game).players.remove(networkPlayer)
        EventsManager.fireEvent(PlayerQuitGameEvent(this, networkPlayer))
        removePlayer(networkPlayer)
    }
    fun addPlayer(networkPlayer: LocalNetworkPlayer)
    fun removePlayer(networkPlayer: LocalNetworkPlayer)

    class PlayerJoinResult(val type: PlayerJoinResultType, val chance: Double, val message: String = "Unknown") {
    }
    enum class PlayerJoinResultType(val success: Boolean) { ACCEPTED(true), DENIED(false) }

    open fun getPreSpawnLocation(player: OfflinePlayer, spectate: Boolean): Location = gameWorld.spawnLocation
    open fun getSpawnLocation(player: LocalNetworkPlayer, spectate: Boolean): Location = gameWorld.spawnLocation

    open fun canShutdown(): Boolean {
        if (this is Lobby) {
            return true
        } else {
            return players.isEmpty()
        }
    }

}

