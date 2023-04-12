package net.tilapiamc.command.args.impl

import net.tilapiamc.command.CommandException
import net.tilapiamc.command.CommandExecution
import net.tilapiamc.command.NetworkCommand
import net.tilapiamc.command.args.CommandArgument
import kotlin.reflect.KProperty

class StringEnumArgument<T>(name: String, val enumValues: (sender: T) -> Collection<String>, val ignoreCase: Boolean, val exposeValues: (sender: T) -> Boolean, isRequired: Boolean): CommandArgument<String, T>(name, isRequired) {



    override fun getValue(any: Any?, property: KProperty<*>): CommandExecution<T>.() -> String? {

        return lambda@{
            val values = enumValues(sender)
            if (getArgString() == null) {
                return@lambda null
            }

            val result = if (ignoreCase) values.firstOrNull { it.lowercase() == getArgString()?.lowercase() } else values.firstOrNull { it == getArgString() }
                ?: throw EnumNotFoundException(getArgString()!!, values, exposeValues(sender))
            result
        }
    }

    override fun tabComplete(sender: T, token: String): Collection<String> {
        if (ignoreCase) {
            return enumValues(sender).filter { it.lowercase().startsWith(token.lowercase()) }
        } else {
            return enumValues(sender).filter { it.startsWith(token) }
        }
    }
}


fun <T> NetworkCommand<T, *>.stringEnumArg(name: String, enumValues: (sender: T) -> Collection<String>, ignoreCase: Boolean = true, exposeValues: (T) -> Boolean = { true }, isRequired: Boolean = true): StringEnumArgument<T> {
    return addArgument(StringEnumArgument(name, enumValues, ignoreCase, exposeValues, isRequired))
}