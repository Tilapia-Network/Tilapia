package net.tilapiamc.dummycore.networking

import kotlinx.coroutines.runBlocking
import net.tilapiamc.api.game.Game
import net.tilapiamc.api.game.ManagedGame
import net.tilapiamc.api.game.lobby.Lobby
import net.tilapiamc.api.game.lobby.ManagedLobby
import net.tilapiamc.api.game.minigame.ManagedMiniGame
import net.tilapiamc.api.game.minigame.MiniGame
import net.tilapiamc.api.networking.GameFinder
import net.tilapiamc.api.player.NetworkPlayer
import net.tilapiamc.dummycore.TilapiaCoreImpl
import java.util.*

class GameFinderImpl(val core: TilapiaCoreImpl): GameFinder {


    override fun findLobbies(lobbyType: String): List<Lobby> {
        return core.localGameManager.getAllLocalGames().filterIsInstance<Lobby>()
    }

    override fun findMiniGames(miniGameType: String): List<MiniGame> {
        return core.localGameManager.getAllLocalGames().filterIsInstance<MiniGame>()
    }

    override fun findLobbiesForPlayer(
        player: NetworkPlayer,
        lobbyType: String,
        forceJoin: Boolean
    ): HashMap<Lobby, ManagedGame.PlayerJoinResult> {
        return HashMap<Lobby, ManagedGame.PlayerJoinResult>().also { out ->
            core.localGameManager.getAllLocalGames().filterIsInstance<ManagedLobby>()
                .map { it to it.couldAddPlayer(player, forceJoin) }
                .forEach { out[it.first] = it.second }
        }
    }

    override fun findMiniGamesForPlayer(
        player: NetworkPlayer,
        miniGameType: String,
        forceJoin: Boolean
    ): HashMap<MiniGame, ManagedGame.PlayerJoinResult> {
        return HashMap<MiniGame, ManagedGame.PlayerJoinResult>().also { out ->
            core.localGameManager.getAllLocalGames().filterIsInstance<ManagedMiniGame>()
                .map { it to it.couldAddPlayer(player, forceJoin) }
                .forEach { out[it.first] = it.second }
        }
    }

    override fun findLobbyToJoin(player: NetworkPlayer, lobbyType: String, forceJoin: Boolean): Lobby? {
        return findAvailableLobbiesForPlayer(player, lobbyType, forceJoin).entries.shuffled().sortedBy { it.value.chance }.map { it.key }.firstOrNull()
    }

    override fun findMiniGameToJoin(player: NetworkPlayer, miniGameType: String, forceJoin: Boolean): MiniGame? {
        return findAvailableMiniGamesForPlayer(player, miniGameType, forceJoin).entries.shuffled().sortedBy { it.value.chance }.map { it.key }.firstOrNull()
    }

    override fun getGameFromID(gameId: UUID): Game? {
        return core.localGameManager.getLocalGameById(gameId) as Game?
    }

    override fun getGameFromShortID(shortGameId: String): Game? {
        return core.localGameManager.getAllLocalGames().firstOrNull { it.shortGameId.lowercase() == shortGameId.lowercase() } as Game?
    }

}