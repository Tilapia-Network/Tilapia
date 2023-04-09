package net.tilapiamc.api.internal

import net.tilapia.api.game.Game
import net.tilapia.api.player.LocalNetworkPlayer
import net.tilapia.api.player.NetworkPlayer
import org.bukkit.entity.Player

interface TilapiaInternal {

    fun sendToGame(player: NetworkPlayer, game: Game?)

    fun createLocalPlayer(bukkitPlayer: Player): LocalNetworkPlayer

}