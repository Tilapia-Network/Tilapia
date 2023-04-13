package net.tilapiamc.core

import net.tilapiamc.api.game.Game
import net.tilapiamc.api.game.ManagedGame
import net.tilapiamc.api.internal.JoinDeniedException
import net.tilapiamc.api.internal.TilapiaInternal
import net.tilapiamc.api.player.LocalNetworkPlayer
import net.tilapiamc.api.player.NetworkPlayer
import net.tilapiamc.language.LanguageCore
import org.bukkit.entity.Player

class TilapiaInternalImpl(val core: TilapiaCoreImpl): TilapiaInternal {
    override fun sendToGame(player: NetworkPlayer, game: Game?, forceJoin: Boolean) {
        // TODO: Communication
        if (player is LocalNetworkPlayer) {
            player.logger.debug("Sending player to ${game?.gameId}")
            if (game != null) {
                player.sendMessage(player.getLanguageBundle()[LanguageCore.SEND_TO_A_GAME].format(game.gameId))
            }
        }
        if (player.currentGame != null && player is LocalNetworkPlayer) {
            if (player.currentGame!!.managed) {
                val managedGame = player.currentGame!! as ManagedGame
                managedGame.remove(player)
            }
        }
        if (game != null) {
            if (game.managed && game is ManagedGame && player.isLocal && player is LocalNetworkPlayer) {
                player.resetPlayerState()
                player.teleport(game.gameWorld.spawnLocation)
                val result = game.couldAdd(player, false)
                if (result.type.success) {
                    game.add(player)
                } else {
                    throw JoinDeniedException(result.message)
                }
            }
            player.currentGameId = game.gameId
        } else {
            player.currentGameId = null
        }

    }



    override fun createLocalPlayer(bukkitPlayer: Player): LocalNetworkPlayer {
        return LocalPlayerImpl(core, bukkitPlayer)
    }


}