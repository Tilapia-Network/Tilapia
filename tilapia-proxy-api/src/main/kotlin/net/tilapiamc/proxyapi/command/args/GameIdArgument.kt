package net.tilapiamc.proxyapi.command.args

import net.tilapiamc.command.ArgumentsContainer
import net.tilapiamc.command.CommandException
import net.tilapiamc.command.CommandExecution
import net.tilapiamc.command.args.CommandArgument
import net.tilapiamc.proxyapi.TilapiaProxyAPI
import net.tilapiamc.proxyapi.game.Game
import kotlin.reflect.KProperty

class GameIdArgument<T>(name: String, val filter: (Game) -> Boolean, isRequired: Boolean = true): CommandArgument<Game, T>(name, isRequired) {


    override fun getValue(any: Any?, property: KProperty<*>): CommandExecution<T>.() -> Game? {
        return lambda@{
            val gameId = getArgString()?:return@lambda null
            val game = TilapiaProxyAPI.instance.gameFinder.getGameFromShortID(gameId)
                ?: throw GameNotFoundException(gameId)
            if (!filter(game)) {
                throw GameNotFoundException(gameId)
            }
            game
        }
    }

    override fun tabComplete(sender: T, token: String): Collection<String> {
        val games = ArrayList<Game>()
        games.addAll(TilapiaProxyAPI.instance.gameFinder.findMiniGames(null))
        games.addAll(TilapiaProxyAPI.instance.gameFinder.findLobbies(null))
        return games.filter { filter(it) }.map { it.shortGameId }.filter { it.lowercase().startsWith(token.lowercase()) }
    }
}

class GameNotFoundException(val gameId: String): CommandException(gameId)

fun <T> ArgumentsContainer<T>.gameIdArg(name: String, filter: (Game) -> Boolean, isRequired: Boolean = true): GameIdArgument<T> {
    return addArgument(GameIdArgument(name, filter, isRequired))
}