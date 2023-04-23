package net.tilapiamc.core

import kotlinx.coroutines.runBlocking
import net.tilapiamc.api.commands.LanguageCommand
import net.tilapiamc.api.game.Game
import net.tilapiamc.api.internal.TilapiaInternal
import net.tilapiamc.api.player.LocalNetworkPlayer
import net.tilapiamc.api.player.NetworkPlayer
import net.tilapiamc.api.player.PlayersManager.getLocalPlayer
import net.tilapiamc.language.LanguageCore
import org.bukkit.entity.Player

class TilapiaInternalImpl(val core: TilapiaCoreImpl): TilapiaInternal {
    override fun sendToGame(player: NetworkPlayer, game: Game, forceJoin: Boolean, spectate: Boolean) {
        if (player is LocalNetworkPlayer) {
            player.logger.info("Sending player to ${game.gameId}")
            player.sendMessage(player.getLanguageBundle()[LanguageCore.SEND_TO_A_GAME].format(game.shortGameId))
        }

        Thread {
            val result = runBlocking {
                core.communication.send(player.uuid, game.gameId, forceJoin, spectate)
            }
            if (!result.success) {
                if (player is Player) {
                    player.sendMessage(player.getLocalPlayer().getLanguageBundle()[LanguageCommand.JOIN_DENIED].format(game.shortGameId, result.message))
                }
            }
        }.start()

    }



    override fun createLocalPlayer(bukkitPlayer: Player): LocalNetworkPlayer {
        return LocalPlayerImpl(core, bukkitPlayer)
    }


}