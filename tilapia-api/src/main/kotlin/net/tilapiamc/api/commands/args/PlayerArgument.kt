package net.tilapiamc.api.commands.args

import net.tilapiamc.api.game.ManagedGame
import net.tilapiamc.api.game.minigame.ManagedMiniGame
import net.tilapiamc.api.player.LocalNetworkPlayer
import net.tilapiamc.api.player.PlayersManager.getLocalPlayer
import net.tilapiamc.command.CommandException
import net.tilapiamc.command.CommandExecution
import net.tilapiamc.command.NetworkCommand
import net.tilapiamc.command.args.CommandArgument
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import kotlin.reflect.KProperty

class PlayerArgument<T>(name: String, isRequired: Boolean = true): CommandArgument<Player, T>(name, isRequired) {


    override fun getValue(any: Any?, property: KProperty<*>): CommandExecution<T>.() -> Player? {
        return lambda@{
            val playerName = getArgString()?:return@lambda null
            if (sender is Player) {
                val currentGame = (sender as Player).getLocalPlayer().currentGame
                if (currentGame is ManagedGame) {
                    return@lambda currentGame.players.filterIsInstance<LocalNetworkPlayer>().firstOrNull { it.name.equals(playerName, true) }
                }
                throw PlayerNotFoundException(playerName)
            } else {
                Bukkit.getPlayer(getArgString())?:throw PlayerNotFoundException(playerName)
            }
        }
    }

    override fun tabComplete(sender: T, token: String): Collection<String> {
        val players = ArrayList<String>()
        if (sender is Player) {
            val currentGame = sender.getLocalPlayer().currentGame
            if (currentGame is ManagedGame) {
                players.addAll(currentGame.players.filterIsInstance<LocalNetworkPlayer>().map { it.name })
            }
        } else {
            players.addAll(Bukkit.getServer().onlinePlayers.map { it.name })
        }
        return players.filter { it.lowercase().startsWith(token) }
    }
}

class PlayerNotFoundException(val playerName: String): CommandException("Player not found: $playerName")

fun <T> NetworkCommand<T, *>.playerArg(name: String, isRequired: Boolean = true): PlayerArgument<T> {
    return addArgument(PlayerArgument(name, isRequired))
}