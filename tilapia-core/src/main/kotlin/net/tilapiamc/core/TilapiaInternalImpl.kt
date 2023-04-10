package net.tilapiamc.core

import net.tilapiamc.api.game.Game
import net.tilapiamc.api.game.GamesManager
import net.tilapiamc.api.game.ManagedGame
import net.tilapiamc.api.game.lobby.Lobby
import net.tilapiamc.api.game.minigame.MiniGame
import net.tilapiamc.api.internal.TilapiaInternal
import net.tilapiamc.api.player.LocalNetworkPlayer
import net.tilapiamc.api.player.NetworkPlayer
import org.bukkit.ChatColor
import org.bukkit.entity.Player

class TilapiaInternalImpl(val core: TilapiaCoreImpl): TilapiaInternal {
    override fun sendToGame(player: NetworkPlayer, game: Game?) {
        if (player is LocalNetworkPlayer) {
            player.logger.debug("Sending player to ${game?.gameId}")
            if (game != null) {
                player.sendMessage("${ChatColor.DARK_GRAY}Sending you to ${ChatColor.GRAY}${game.gameId}")
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
                val result = game.preAdd(player)
                if (result.type.success) {
                    game.add(player)
                } else {
                    error("Could not add player: ${result.message}")
                }
            }
            player.currentGameId = game.gameId
        } else {
            player.currentGameId = null
        }

    }

    override fun findMiniGameToJoin(player: NetworkPlayer, miniGameType: String): MiniGame? {
        return GamesManager.getAllGames().filterIsInstance<MiniGame>().firstOrNull { it.miniGameType == miniGameType }
    }

    override fun findLobbyToJoin(player: NetworkPlayer, lobbyType: String): Lobby? {
        return GamesManager.getAllGames().filterIsInstance<Lobby>().firstOrNull { it.lobbyType == lobbyType }
    }


    override fun createLocalPlayer(bukkitPlayer: Player): LocalNetworkPlayer {
        return LocalPlayerImpl(core, bukkitPlayer)
    }


}