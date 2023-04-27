package net.tilapiamc.dummycore

import kotlinx.coroutines.runBlocking
import net.tilapiamc.api.commands.LanguageCommand
import net.tilapiamc.api.game.Game
import net.tilapiamc.api.game.GameType
import net.tilapiamc.api.game.ManagedGame
import net.tilapiamc.api.game.minigame.ManagedMiniGame
import net.tilapiamc.api.internal.TilapiaInternal
import net.tilapiamc.api.player.LocalNetworkPlayer
import net.tilapiamc.api.player.NetworkPlayer
import net.tilapiamc.api.player.PlayersManager.getLocalPlayer
import net.tilapiamc.language.LanguageCore
import org.bukkit.entity.Player

class TilapiaInternalImpl(val core: TilapiaCoreImpl): TilapiaInternal {
    override fun sendToGame(player: NetworkPlayer, game: Game, forceJoin: Boolean, spectate: Boolean) {
        if (player is LocalNetworkPlayer) {
            player.logger.debug("Sending player to ${game.gameId}")
            if (game != null) {
                player.sendMessage(player.getLanguageBundle()[LanguageCore.SEND_TO_A_GAME].format(game.shortGameId))
            }
        }
        if (player is LocalNetworkPlayer && player.currentGame != null) {
            if (player.currentGame!!.managed) {
                val managedGame = player.currentGame!! as ManagedGame
                managedGame.remove(player)
            }
        }
        if (spectate && game.gameType != GameType.MINIGAME) {
            throw JoinDeniedException(game.shortGameId, "The game doesn't accept spectators")
        }
        if (game.managed && game is ManagedGame && player.isLocal && player is LocalNetworkPlayer) {
            player.resetPlayerState()
            player.teleport(game.gameWorld.spawnLocation)
            if (spectate) {
                (game as ManagedMiniGame).addSpectator(player)
            } else {
                val result = game.couldAdd(player, forceJoin)
                if (result.type.success) {
                    game.add(player)
                } else {
                    throw JoinDeniedException(game.shortGameId, result.message)
                }
            }

        }
        if (player is LocalNetworkPlayer) {
            player.currentGameId = game.gameId
        }

    }



    override fun createLocalPlayer(bukkitPlayer: Player): LocalNetworkPlayer {
        return LocalPlayerImpl(core, bukkitPlayer)
    }


}
class JoinDeniedException(val gameId: String, val reason: String): RuntimeException()
