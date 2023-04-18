package net.tilapiamc.proxycore.networking

import kotlinx.coroutines.runBlocking
import net.tilapiamc.proxyapi.GameFinder
import net.tilapiamc.proxyapi.PlayerJoinResult
import net.tilapiamc.proxyapi.game.Game
import net.tilapiamc.proxyapi.game.Lobby
import net.tilapiamc.proxyapi.game.MiniGame
import net.tilapiamc.proxyapi.player.NetworkPlayer
import net.tilapiamc.proxycore.TilapiaProxyCore
import java.util.*

class GameFinderImpl(val core: TilapiaProxyCore): GameFinder {


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
    ): HashMap<Lobby, PlayerJoinResult> {
        return HashMap<Lobby, PlayerJoinResult>().also { out ->
            runBlocking {
                core.communication.getGamesForPlayer(
                    player.uuid,
                    lobbyType = lobbyType,
                    forceJoin = forceJoin
                ).entries.forEach {
                    val result = it.value
                    out[NetworkLobbyImpl(core.communication, it.key.lobby!!)] =
                        PlayerJoinResult(result.success, result.chance, result.message)
                }
            }
        }
    }

    override fun findMiniGamesForPlayer(
        player: NetworkPlayer,
        miniGameType: String,
        forceJoin: Boolean
    ): HashMap<MiniGame, PlayerJoinResult> {
        return HashMap<MiniGame, PlayerJoinResult>().also { out ->
            runBlocking {
                core.communication.getGamesForPlayer(
                    player.uuid,
                    miniGameType = miniGameType,
                    forceJoin = forceJoin
                ).entries.forEach {
                    val result = it.value
                    out[NetworkMiniGameImpl(core.communication, it.key.miniGame!!)] =
                        PlayerJoinResult(result.success, result.chance, result.message)
                }
            }
        }
    }


    override fun getGameFromID(gameId: UUID): Game? {
        return runBlocking {
            core.communication.getGames(gameIdPrefix = gameId.toString())[0].toGame(core.communication)
        }
    }

    override fun getGameFromShortID(shortGameId: String): Game? {
        return runBlocking {
            core.communication.getGames(gameIdPrefix = shortGameId)[0].toGame(core.communication)
        }
    }
}