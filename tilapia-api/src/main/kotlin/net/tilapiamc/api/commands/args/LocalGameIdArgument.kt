package net.tilapiamc.api.commands.args

import net.tilapiamc.api.TilapiaCore
import net.tilapiamc.api.game.GameType
import net.tilapiamc.api.game.ManagedGame
import net.tilapiamc.api.player.LocalNetworkPlayer
import net.tilapiamc.api.player.PlayersManager.getLocalPlayer
import net.tilapiamc.command.ArgumentsContainer
import net.tilapiamc.command.CommandException
import net.tilapiamc.command.CommandExecution
import net.tilapiamc.command.args.CommandArgument
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import kotlin.reflect.KProperty

class LocalGameIdArgument<T>(name: String, val filter: (ManagedGame) -> Boolean, isRequired: Boolean = true): CommandArgument<ManagedGame, T>(name, isRequired) {


    override fun getValue(any: Any?, property: KProperty<*>): CommandExecution<T>.() -> ManagedGame? {
        return lambda@{
            val gameId = getArgString()?:return@lambda null
            val game = TilapiaCore.instance.localGameManager.getAllLocalGames().filter { filter(it) }.firstOrNull { it.shortGameId == gameId }
                ?: throw GameNotFoundException(gameId)
            game
        }
    }

    override fun tabComplete(sender: T, token: String): Collection<String> {
        return TilapiaCore.instance.localGameManager.getAllLocalGames().filter { filter(it) }.map { it.shortGameId }.filter { it.lowercase().startsWith(token) }
    }
}

class GameNotFoundException(val gameId: String): CommandException(gameId)

fun <T> ArgumentsContainer<T>.localGameIdArg(name: String, filter: (ManagedGame) -> Boolean, isRequired: Boolean = true): LocalGameIdArgument<T> {
    return addArgument(LocalGameIdArgument(name, filter, isRequired))
}