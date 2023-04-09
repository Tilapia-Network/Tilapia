package net.tilapiamc.api.game

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

}