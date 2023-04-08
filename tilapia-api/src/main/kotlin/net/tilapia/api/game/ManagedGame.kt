package net.tilapia.api.game

import net.tilapia.api.player.LocalNetworkPlayer
import net.tilapia.api.player.NetworkPlayer
import org.bukkit.World

interface ManagedGame {

    companion object {
        const val REQUIRES_PERMISSION = -1.0
        const val DENIED = 0.0
    }

    val gameWorld: World
    fun end()

    fun couldAddPlayer(networkPlayer: NetworkPlayer): Double

    fun preAddPlayer(networkPlayer: NetworkPlayer): PlayerJoinResult
    fun addPlayer(networkPlayer: LocalNetworkPlayer)
    fun removePlayer(networkPlayer: LocalNetworkPlayer)

    class PlayerJoinResult(val type: PlayerJoinResultType, val message: String = "")
    enum class PlayerJoinResultType(val success: Boolean) { OK(true), DENIED(false) }

}

