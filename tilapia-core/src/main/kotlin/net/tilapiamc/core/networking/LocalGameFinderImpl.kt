package net.tilapiamc.core.networking

import net.tilapiamc.api.game.Game
import net.tilapiamc.api.game.ManagedGame
import net.tilapiamc.api.game.lobby.Lobby
import net.tilapiamc.api.game.minigame.MiniGame
import net.tilapiamc.api.networking.GameFinder
import net.tilapiamc.api.player.NetworkPlayer
import net.tilapiamc.core.TilapiaCoreImpl
import java.util.*

class LocalGameFinderImpl(val core: TilapiaCoreImpl): GameFinder {


    override fun findLobbies(lobbyType: String): List<Lobby> {
        return core.communication.getLobbies().map { NetworkLobbyImpl(core.communication, it) }
    }

    override fun findMiniGames(miniGameType: String): List<MiniGame> {
        return core.communication.getMiniGames().map { NetworkMiniGameImpl(core.communication, it) }
    }

    override fun findLobbiesForPlayer(
        player: NetworkPlayer,
        lobbyType: String,
        forceJoin: Boolean
    ): HashMap<Lobby, ManagedGame.PlayerJoinResult> {
        return HashMap<Lobby, ManagedGame.PlayerJoinResult>().also { out ->
            core.communication.getLobbiesForPlayer(player.uuid).entries.forEach {
                val result = it.value
                out[NetworkLobbyImpl(core.communication, it.key)] = ManagedGame.PlayerJoinResult(if (result.success) ManagedGame.PlayerJoinResultType.ACCEPTED else ManagedGame.PlayerJoinResultType.DENIED, result.chance, result.message)
            }
        }
    }

    override fun findMiniGamesForPlayer(
        player: NetworkPlayer,
        miniGameType: String,
        forceJoin: Boolean
    ): HashMap<MiniGame, ManagedGame.PlayerJoinResult> {
        return HashMap<MiniGame, ManagedGame.PlayerJoinResult>().also { out ->
            core.communication.getMiniGamesForPlayer(player.uuid).entries.forEach {
                val result = it.value
                out[NetworkMiniGameImpl(core.communication, it.key)] = ManagedGame.PlayerJoinResult(if (result.success) ManagedGame.PlayerJoinResultType.ACCEPTED else ManagedGame.PlayerJoinResultType.DENIED, result.chance, result.message)
            }
        }
    }



    override fun getGameFromID(gameId: UUID): Game? {
        return core.localGameManager.getLocalGameById(gameId) as Game?
    }

    override fun getGameFromShortID(shortGameId: String): Game? {
        return core.localGameManager.getAllLocalGames().firstOrNull { it.shortGameId.lowercase() == shortGameId.lowercase() } as Game?
    }

}