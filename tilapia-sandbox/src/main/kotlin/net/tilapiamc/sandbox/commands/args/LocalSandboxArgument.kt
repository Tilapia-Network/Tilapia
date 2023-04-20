package net.tilapiamc.sandbox.commands.args

import net.tilapiamc.api.TilapiaCore
import net.tilapiamc.api.game.lobby.Lobby
import net.tilapiamc.command.ArgumentsContainer
import net.tilapiamc.command.CommandException
import net.tilapiamc.command.CommandExecution
import net.tilapiamc.command.args.CommandArgument
import net.tilapiamc.sandbox.SandboxProperties
import net.tilapiamc.sandbox.TilapiaSandbox
import kotlin.reflect.KProperty

class LocalSandboxArgument<S>(name: String, isRequired: Boolean = false): CommandArgument<TilapiaSandbox, S>(name, isRequired) {

    override fun getValue(any: Any?, property: KProperty<*>): CommandExecution<S>.() -> TilapiaSandbox? {
        return lambda@{
            val worldName = getArgString()?:return@lambda null
            val sandBox = TilapiaCore.instance.localGameManager.getAllLocalGames().filterIsInstance<TilapiaSandbox>()
                .filter { it.hasProperty(SandboxProperties.SANDBOX_WORLD) }
                .firstOrNull { it.getProperty(SandboxProperties.SANDBOX_WORLD)!!.asString == worldName }
                ?:throw SandBoxNotFoundException(worldName)

            sandBox
        }
    }

    override fun tabComplete(sender: S, token: String): Collection<String> {
        return TilapiaCore.instance.localGameManager.getAllLocalGames()
            .filterIsInstance<TilapiaSandbox>()
            .filter { it.hasProperty(SandboxProperties.SANDBOX_WORLD) }
            .map { it.getProperty(SandboxProperties.SANDBOX_WORLD)!!.asString }
            .filter { it.lowercase().startsWith(token) }
    }
}

fun <T> ArgumentsContainer<T>.localSandBoxArg(name: String, isRequired: Boolean = true): LocalSandboxArgument<T> {
    return addArgument(LocalSandboxArgument(name, isRequired))
}
class SandboxArgument<S>(name: String, isRequired: Boolean = false): CommandArgument<Lobby, S>(name, isRequired) {

    override fun getValue(any: Any?, property: KProperty<*>): CommandExecution<S>.() -> Lobby? {
        return lambda@{
            val worldName = getArgString()?:return@lambda null
            val sandBox = TilapiaCore.instance.gameFinder.findLobbies("sandbox")
                .filter { it.hasProperty(SandboxProperties.SANDBOX_WORLD) }
                .firstOrNull { it.getProperty(SandboxProperties.SANDBOX_WORLD)!!.asString == worldName }
                ?:throw SandBoxNotFoundException(worldName)

            sandBox
        }
    }

    override fun tabComplete(sender: S, token: String): Collection<String> {
        return TilapiaCore.instance.gameFinder.findLobbies("sandbox")
            .filter { it.hasProperty(SandboxProperties.SANDBOX_WORLD) }
            .map { it.getProperty(SandboxProperties.SANDBOX_WORLD)!!.asString }
            .filter { it.lowercase().startsWith(token) }
    }
}

fun <T> ArgumentsContainer<T>.sandBoxArg(name: String, isRequired: Boolean = true): SandboxArgument<T> {
    return addArgument(SandboxArgument(name, isRequired))
}
class SandBoxNotFoundException(val worldName: String): CommandException(worldName)
