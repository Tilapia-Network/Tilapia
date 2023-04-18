package net.tilapiamc.core.networking

import kotlinx.coroutines.runBlocking
import net.tilapiamc.api.game.Game
import net.tilapiamc.api.game.ManagedGame
import net.tilapiamc.api.game.lobby.Lobby
import net.tilapiamc.api.game.minigame.MiniGame
import net.tilapiamc.api.networking.GameFinder
import net.tilapiamc.api.player.NetworkPlayer
import net.tilapiamc.core.TilapiaCoreImpl
import java.util.*

class GameFinderImpl(val core: TilapiaCoreImpl): GameFinder {


    override fun findLobbies(lobbyType: String): List<Lobby> {
        return runBlocking {
            core.communication.getGames(lobbyType).map { NetworkLobbyImpl(core.communication, it.lobby!!) }
        }
    }

    override fun findMiniGames(miniGameType: String): List<MiniGame> {
        return runBlocking {
            core.communication.getGames(miniGameType).map { NetworkMiniGameImpl(core.communication, it.miniGame!!) }
        }
    }

    override fun findLobbiesForPlayer(
        player: NetworkPlayer,
        lobbyType: String,
        forceJoin: Boolean
    ): HashMap<Lobby, ManagedGame.PlayerJoinResult> {
        return HashMap<Lobby, ManagedGame.PlayerJoinResult>().also { out ->
            runBlocking {
                core.communication.getGamesForPlayer(player.uuid, lobbyType = lobbyType, forceJoin = forceJoin).entries.forEach {
                    val result = it.value
                    out[NetworkLobbyImpl(core.communication, it.key.lobby!!)] = ManagedGame.PlayerJoinResult(if (result.success) ManagedGame.PlayerJoinResultType.ACCEPTED else ManagedGame.PlayerJoinResultType.DENIED, result.chance, result.message)
                }
            }
        }
    }

    override fun findMiniGamesForPlayer(
        player: NetworkPlayer,
        miniGameType: String,
        forceJoin: Boolean
    ): HashMap<MiniGame, ManagedGame.PlayerJoinResult> {
        return HashMap<MiniGame, ManagedGame.PlayerJoinResult>().also { out ->
            runBlocking {
                core.communication.getGamesForPlayer(player.uuid, miniGameType = miniGameType, forceJoin = forceJoin).entries.forEach {
                    val result = it.value
                    out[NetworkMiniGameImpl(core.communication, it.key.miniGame!!)] = ManagedGame.PlayerJoinResult(if (result.success) ManagedGame.PlayerJoinResultType.ACCEPTED else ManagedGame.PlayerJoinResultType.DENIED, result.chance, result.message)
                }
            }
        }
    }



    override fun getGameFromID(gameId: UUID): Game? {
        return core.localGameManager.getLocalGameById(gameId) as Game?
            ?: return runBlocking {
                core.communication.getGames(gameIdPrefix = gameId.toString())[0].toGame(core.communication)
            }
    }

    override fun getGameFromShortID(shortGameId: String): Game? {
        return core.localGameManager.getAllLocalGames().firstOrNull { it.shortGameId.lowercase() == shortGameId.lowercase() } as Game?
            ?: return runBlocking {
                core.communication.getGames(gameIdPrefix = shortGameId)[0].toGame(core.communication)
            }
    }

}