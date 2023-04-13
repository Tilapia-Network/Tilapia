package net.tilapiamc.command.args.impl

import net.tilapiamc.command.ArgumentsContainer
import net.tilapiamc.command.CommandException
import net.tilapiamc.command.CommandExecution
import net.tilapiamc.command.NetworkCommand
import net.tilapiamc.command.args.CommandArgument
import kotlin.reflect.KProperty

class EnumArgument<T, E: Enum<*>>(val enumClass: Class<E>, name: String, val allowIndex: (sender: T) -> Boolean, val exposeValues: (sender: T) -> Boolean, isRequired: Boolean): CommandArgument<E, T>(name, isRequired) {



    override fun getValue(any: Any?, property: KProperty<*>): CommandExecution<T>.() -> E? {

        return lambda@{
            if (getArgString() == null) {
                return@lambda null
            }
            val result = enumClass.enumConstants.firstOrNull { it.name.lowercase() == getArgString()?.lowercase() || (allowIndex(sender) && it.ordinal.toString() == getArgString()) }
                ?: throw EnumNotFoundException(getArgString()!!, enumClass.enumConstants.map { it.name }, exposeValues(sender))
            result
        }
    }

    override fun tabComplete(sender: T, token: String): Collection<String> {
        if (exposeValues(sender)) {
            return enumClass.enumConstants.map { it.name }.filter { it.lowercase().startsWith(token.lowercase()) }
        } else {
            return emptyList()
        }
    }
}

class EnumNotFoundException(val value: String, val enumValues: Collection<String>, val exposeValues: Boolean)
    :CommandException("Unknown enum value: $value")

inline fun <T, reified E: Enum<*>> ArgumentsContainer<T>.enumArg(name: String, noinline allowIndex: (T) -> Boolean = { false }, noinline exposeValues: (T) -> Boolean = { true }, isRequired: Boolean = true): EnumArgument<T, E> {
    return addArgument(EnumArgument<T, E>(E::class.java, name, allowIndex, exposeValues, isRequired))
}