package net.tilapiamc.sandbox.commands.args

import net.tilapiamc.command.ArgumentsContainer
import net.tilapiamc.command.CommandException
import net.tilapiamc.command.CommandExecution
import net.tilapiamc.command.NetworkCommand
import net.tilapiamc.command.args.CommandArgument
import org.bukkit.Bukkit
import kotlin.reflect.KProperty

class WorldNameArgument<S>(name: String, val requireLoaded: Boolean, isRequired: Boolean = false): CommandArgument<String, S>(name, isRequired) {

    override fun getValue(any: Any?, property: KProperty<*>): CommandExecution<S>.() -> String? {
        return lambda@{
            val worldName = getArgString()?:return@lambda null
            if (requireLoaded) {
                if (!Bukkit.getWorlds().any { it.name == worldName }) {
                    throw WorldNotFoundException(worldName)
                }
            }

            worldName
        }
    }

    override fun tabComplete(sender: S, token: String): Collection<String> {
        val out = ArrayList<String>()
        if (requireLoaded) {
            out.addAll(Bukkit.getWorlds().map { it.name })
        }
        return out.filter { it.lowercase().startsWith(token) }
    }
}

fun <T> ArgumentsContainer<T>.worldNameArg(name: String, requireLoaded: Boolean, isRequired: Boolean = true): WorldNameArgument<T> {
    return addArgument(WorldNameArgument(name, requireLoaded, isRequired))
}
class WorldNotFoundException(val worldName: String): CommandException(worldName)
