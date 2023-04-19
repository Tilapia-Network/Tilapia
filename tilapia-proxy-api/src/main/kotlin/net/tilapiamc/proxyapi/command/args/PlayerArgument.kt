package net.tilapiamc.proxyapi.command.args

import com.velocitypowered.api.proxy.Player
import net.tilapiamc.command.ArgumentsContainer
import net.tilapiamc.command.CommandException
import net.tilapiamc.command.CommandExecution
import net.tilapiamc.command.args.CommandArgument
import net.tilapiamc.proxyapi.TilapiaProxyAPI
import kotlin.jvm.optionals.getOrNull
import kotlin.reflect.KProperty

class PlayerArgument<T>(name: String, isRequired: Boolean = true): CommandArgument<Player, T>(name, isRequired) {


    override fun getValue(any: Any?, property: KProperty<*>): CommandExecution<T>.() -> Player? {
        return lambda@{
            val playerName = getArgString()?:return@lambda null
            TilapiaProxyAPI.instance.proxy.getPlayer(playerName).getOrNull()?:throw PlayerNotFoundException(playerName)
        }
    }

    override fun tabComplete(sender: T, token: String): Collection<String> {
        return emptyList()
    }
}

class PlayerNotFoundException(val playerName: String): CommandException("Player not found: $playerName")

fun <T> ArgumentsContainer<T>.playerArg(name: String, isRequired: Boolean = true): PlayerArgument<T> {
    return addArgument(PlayerArgument(name, isRequired))
}