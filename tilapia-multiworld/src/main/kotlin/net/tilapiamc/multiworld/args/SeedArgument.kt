package net.tilapiamc.multiworld.args

import net.tilapiamc.command.ArgumentsContainer
import net.tilapiamc.command.CommandExecution
import net.tilapiamc.command.args.CommandArgument
import kotlin.reflect.KProperty

class SeedArgument<S>(name: String, isRequired: Boolean = false): CommandArgument<String, S>(name, isRequired) {

    override fun getValue(any: Any?, property: KProperty<*>): CommandExecution<S>.() -> String? {
        return lambda@{
            val seed = getArgString()?:return@lambda null
            if (seed == "default") {
                return@lambda null
            }
            seed
        }
    }

    override fun tabComplete(sender: S, token: String): Collection<String> {
        return arrayOf("default").filter { it.lowercase().startsWith(token) }
    }
}

fun <T> ArgumentsContainer<T>.seedArg(name: String, isRequired: Boolean = true): SeedArgument<T> {
    return addArgument(SeedArgument(name, isRequired))
}

