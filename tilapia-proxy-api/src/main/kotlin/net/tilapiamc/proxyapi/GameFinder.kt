package net.tilapiamc.proxyapi

import net.tilapiamc.proxyapi.game.Game
import net.tilapiamc.proxyapi.game.Lobby
import net.tilapiamc.proxyapi.game.MiniGame
import net.tilapiamc.proxyapi.player.NetworkPlayer
import java.util.*

interface GameFinder {

    fun findLobbies(lobbyType: String?): List<Lobby>
    fun findMiniGames(miniGameType: String?): List<MiniGame>

    fun findLobbiesForPlayer(player: NetworkPlayer, lobbyType: String, forceJoin: Boolean): Map<Lobby, PlayerJoinResult>
    fun findMiniGamesForPlayer(player: NetworkPlayer, miniGameType: String, forceJoin: Boolean): Map<MiniGame, PlayerJoinResult>

    fun findAvailableLobbiesForPlayer(player: NetworkPlayer, lobbyType: String, forceJoin: Boolean): Map<Lobby, PlayerJoinResult> {
        return findLobbiesForPlayer(player, lobbyType, forceJoin).filter { it.value.success }
    }
    fun findAvailableMiniGamesForPlayer(player: NetworkPlayer, miniGameType: String, forceJoin: Boolean): Map<MiniGame, PlayerJoinResult> {
        return findMiniGamesForPlayer(player, miniGameType, forceJoin).filter { it.value.success }
    }

    fun findLobbyToJoin(player: NetworkPlayer, lobbyType: String, forceJoin: Boolean): Lobby? {
        return findAvailableLobbiesForPlayer(player, lobbyType, forceJoin).entries.sortedBy { it.value.chance }.map { it.key }.firstOrNull()
    }

    fun findMiniGameToJoin(player: NetworkPlayer, miniGameType: String, forceJoin: Boolean): MiniGame? {
        return findAvailableMiniGamesForPlayer(player, miniGameType, forceJoin).entries.sortedBy { it.value.chance }.map { it.key }.firstOrNull()
    }

    fun getGameFromID(gameId: UUID): Game?
    fun getGameFromShortID(shortGameId: String): Game?

}

data class PlayerJoinResult(val success: Boolean, val chance: Double, val message: String)