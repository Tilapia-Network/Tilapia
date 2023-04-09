package net.tilapiamc.api.game

import net.tilapiamc.api.events.EventsManager
import net.tilapiamc.api.events.game.PlayerJoinGameEvent
import net.tilapiamc.api.events.game.PlayerPreJoinGameEvent
import net.tilapiamc.api.events.game.PlayerQuitGameEvent
import net.tilapiamc.api.player.LocalNetworkPlayer
import net.tilapiamc.api.player.NetworkPlayer
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bukkit.World
import java.util.UUID

interface ManagedGame: IGame {

    companion object {
        const val REQUIRES_PERMISSION = -1.0
        const val DENIED = 0.0
    }

    val logger: Logger

    val gameWorld: World
    fun end()

    fun getManagedGameId(): UUID

    fun couldAddPlayer(networkPlayer: NetworkPlayer): Double

    fun preAdd(networkPlayer: NetworkPlayer): PlayerJoinResult {
        val event = PlayerPreJoinGameEvent(this, networkPlayer, preAddPlayer(networkPlayer))
        EventsManager.fireEvent(event)
        return event.result
    }
    fun preAddPlayer(networkPlayer: NetworkPlayer): PlayerJoinResult
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

    class PlayerJoinResult(val type: PlayerJoinResultType, val message: String = "")
    enum class PlayerJoinResultType(val success: Boolean) { ACCEPTED(true), DENIED(false) }

}

