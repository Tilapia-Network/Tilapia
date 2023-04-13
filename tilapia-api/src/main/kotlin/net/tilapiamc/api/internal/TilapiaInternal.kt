package net.tilapiamc.api.internal

import net.tilapiamc.api.game.Game
import net.tilapiamc.api.game.lobby.Lobby
import net.tilapiamc.api.game.minigame.MiniGame
import net.tilapiamc.api.player.LocalNetworkPlayer
import net.tilapiamc.api.player.NetworkPlayer
import org.bukkit.entity.Player

interface TilapiaInternal {

    fun sendToGame(player: NetworkPlayer, game: Game?, forceJoin: Boolean)

    fun createLocalPlayer(bukkitPlayer: Player): LocalNetworkPlayer

}

class JoinDeniedException(val reason: String): RuntimeException()