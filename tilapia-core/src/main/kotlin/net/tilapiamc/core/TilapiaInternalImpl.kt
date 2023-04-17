package net.tilapiamc.core

import kotlinx.coroutines.runBlocking
import net.tilapiamc.api.game.Game
import net.tilapiamc.api.internal.TilapiaInternal
import net.tilapiamc.api.player.LocalNetworkPlayer
import net.tilapiamc.api.player.NetworkPlayer
import org.bukkit.entity.Player

class TilapiaInternalImpl(val core: TilapiaCoreImpl): TilapiaInternal {
    override fun sendToGame(player: NetworkPlayer, game: Game, forceJoin: Boolean, spectate: Boolean) {
        if (player is LocalNetworkPlayer) {
            player.logger.debug("Sending player to ${game.gameId}")
//            if (game != null) {
//                player.sendMessage(player.getLanguageBundle()[LanguageCore.SEND_TO_A_GAME].format(game.gameId))
//            }
        }

        runBlocking {
            core.communication.send(player.uuid, game.gameId, forceJoin, spectate)
        }

    }



    override fun createLocalPlayer(bukkitPlayer: Player): LocalNetworkPlayer {
        return LocalPlayerImpl(core, bukkitPlayer)
    }


}