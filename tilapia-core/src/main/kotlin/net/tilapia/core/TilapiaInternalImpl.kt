package net.tilapia.core

import net.tilapia.api.game.Game
import net.tilapia.api.game.ManagedGame
import net.tilapia.api.internal.TilapiaInternal
import net.tilapia.api.player.LocalNetworkPlayer
import net.tilapia.api.player.NetworkPlayer
import org.bukkit.Location
import org.bukkit.entity.Player

// TODO: Test implementation
class TilapiaInternalImpl(val core: TilapiaCoreImpl): TilapiaInternal {
    override fun sendToGame(player: NetworkPlayer, game: Game) {
        if (player.currentGame != null && player is LocalNetworkPlayer) {
            if (player.currentGame!!.managed) {
                val managedGame = player.currentGame!! as ManagedGame
                managedGame.removePlayer(player)
            }
        }
        if (game.managed && game is ManagedGame && player.isLocal && player is LocalNetworkPlayer) {
            player.resetPlayerState()
            player.teleport(game.gameWorld.spawnLocation)
            val result = game.preAddPlayer(player)
            if (result.type.success) {
                game.addPlayer(player)
            } else {
                error("Could not add player: ${result.message}")
            }
        }
        player.currentGameId = game.gameId
    }


    override fun createLocalPlayer(bukkitPlayer: Player): LocalNetworkPlayer {
        return LocalPlayerImpl(core, bukkitPlayer)
    }
}