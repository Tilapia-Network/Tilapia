package net.tilapiamc.api.internal

import net.tilapiamc.api.game.Game
import net.tilapiamc.api.game.lobby.Lobby
import net.tilapiamc.api.game.minigame.MiniGame
import net.tilapiamc.api.player.LocalNetworkPlayer
import net.tilapiamc.api.player.NetworkPlayer
import org.bukkit.entity.Player

interface TilapiaInternal {

    fun sendToGame(player: NetworkPlayer, game: Game?)
    fun findMiniGameToJoin(player: NetworkPlayer, miniGameType: String): MiniGame?
    fun findLobbyToJoin(player: NetworkPlayer, lobbyType: String): Lobby?

    fun createLocalPlayer(bukkitPlayer: Player): LocalNetworkPlayer

}