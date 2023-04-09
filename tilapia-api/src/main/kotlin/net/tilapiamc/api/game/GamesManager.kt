package net.tilapiamc.api.game

import net.tilapiamc.api.events.annotation.Subscribe
import net.tilapiamc.api.player.PlayersManager.getLocalPlayer
import org.bukkit.event.player.PlayerQuitEvent
import java.util.UUID

object GamesManager {

    private val games = HashMap<UUID, Game>()

    fun registerManagedGame(game: Game) {
        games[game.gameId] = game
    }
    fun unregisterManagedGame(uuid: UUID) {
        games.remove(uuid)
    }

    fun getAllGames(): Set<Game> = games.values.toSet()
    fun getGameById(uuid: UUID): Game? = games[uuid]

    @Subscribe("GamesManager-onPlayerQuit")
    fun onPlayerQuit(event: PlayerQuitEvent) {
        for (game in games.values.filter { event.player.uniqueId in it.players.map { it.uuid } }) {
            if (game is ManagedGame) {
                game.remove(event.player.getLocalPlayer())
            }
        }
    }

}