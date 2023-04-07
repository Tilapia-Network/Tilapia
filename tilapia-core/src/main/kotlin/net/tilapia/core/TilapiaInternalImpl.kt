package net.tilapia.core

import net.tilapia.api.game.Game
import net.tilapia.api.internal.TilapiaInternal
import net.tilapia.api.player.LocalNetworkPlayer
import net.tilapia.api.player.NetworkPlayer
import org.bukkit.entity.Player

class TilapiaInternalImpl(val core: TilapiaCoreImpl): TilapiaInternal {
    override fun sendToGame(player: NetworkPlayer, game: Game) {
        player.currentGameId = game.gameId
        if (game.managed) {
        }
    }


    override fun createLocalPlayer(bukkitPlayer: Player): LocalNetworkPlayer {
        return LocalPlayerImpl(core, bukkitPlayer)
    }
}