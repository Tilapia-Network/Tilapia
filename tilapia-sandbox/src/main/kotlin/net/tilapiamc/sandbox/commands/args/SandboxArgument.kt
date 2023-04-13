package net.tilapiamc.sandbox.commands.args

import net.tilapiamc.api.TilapiaCore
import net.tilapiamc.command.ArgumentsContainer
import net.tilapiamc.command.CommandException
import net.tilapiamc.command.CommandExecution
import net.tilapiamc.command.NetworkCommand
import net.tilapiamc.command.args.CommandArgument
import net.tilapiamc.sandbox.TilapiaSandbox
import org.bukkit.Bukkit
import kotlin.reflect.KProperty

class SandboxArgument<S>(name: String, isRequired: Boolean = false): CommandArgument<TilapiaSandbox, S>(name, isRequired) {

    override fun getValue(any: Any?, property: KProperty<*>): CommandExecution<S>.() -> TilapiaSandbox? {
        return lambda@{
            val worldName = getArgString()?:return@lambda null
            val sandBox = TilapiaCore.instance.localGameManager.getAllLocalGames().filterIsInstance<TilapiaSandbox>().firstOrNull { it.gameWorld.name == worldName }
                ?:throw SandBoxNotFoundException(worldName)

            sandBox
        }
    }

    override fun tabComplete(sender: S, token: String): Collection<String> {
        return TilapiaCore.instance.localGameManager.getAllLocalGames()
            .filterIsInstance<TilapiaSandbox>()
            .map { it.gameWorld.name }
            .filter { it.lowercase().startsWith(token) }
    }
}

fun <T> ArgumentsContainer<T>.sandBoxArg(name: String, isRequired: Boolean = true): SandboxArgument<T> {
    return addArgument(SandboxArgument(name, isRequired))
}
class SandBoxNotFoundException(val worldName: String): CommandException(worldName)
