package net.tilapiamc.api.networking

import net.tilapiamc.api.game.Game
import net.tilapiamc.api.game.ManagedGame
import net.tilapiamc.api.game.lobby.Lobby
import net.tilapiamc.api.game.minigame.MiniGame
import net.tilapiamc.api.player.NetworkPlayer
import java.util.*

interface GameFinder {

    fun findLobbies(lobbyType: String): List<Lobby>
    fun findMiniGames(miniGameType: String): List<MiniGame>

    fun findLobbiesForPlayer(player: NetworkPlayer, lobbyType: String, forceJoin: Boolean): Map<Lobby, ManagedGame.PlayerJoinResult>
    fun findMiniGamesForPlayer(player: NetworkPlayer, miniGameType: String, forceJoin: Boolean): Map<MiniGame, ManagedGame.PlayerJoinResult>

    fun findAvailableLobbiesForPlayer(player: NetworkPlayer, lobbyType: String, forceJoin: Boolean): Map<Lobby, ManagedGame.PlayerJoinResult> {
        return findLobbiesForPlayer(player, lobbyType, forceJoin).filter { it.value.type.success }
    }
    fun findAvailableMiniGamesForPlayer(player: NetworkPlayer, miniGameType: String, forceJoin: Boolean): Map<MiniGame, ManagedGame.PlayerJoinResult> {
        return findMiniGamesForPlayer(player, miniGameType, forceJoin).filter { it.value.type.success }
    }

    fun findLobbyToJoin(player: NetworkPlayer, lobbyType: String, forceJoin: Boolean): Lobby? {
        return findAvailableLobbiesForPlayer(player, lobbyType, forceJoin).entries.shuffled().sortedBy { it.value.chance }.map { it.key }.firstOrNull()
    }

    fun findMiniGameToJoin(player: NetworkPlayer, miniGameType: String, forceJoin: Boolean): MiniGame? {
        return findAvailableMiniGamesForPlayer(player, miniGameType, forceJoin).entries.shuffled().sortedBy { it.value.chance }.map { it.key }.firstOrNull()
    }

    fun getGameFromID(gameId: UUID): Game?
    fun getGameFromShortID(shortGameId: String): Game?

}