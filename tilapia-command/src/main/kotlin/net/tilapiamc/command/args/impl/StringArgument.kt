package net.tilapiamc.command.args.impl

import net.tilapiamc.command.ArgumentsContainer
import net.tilapiamc.command.CommandExecution
import net.tilapiamc.command.NetworkCommand
import net.tilapiamc.command.args.CommandArgument
import kotlin.reflect.KProperty

class StringArgument<T>(name: String, isRequired: Boolean = true): CommandArgument<String, T>(name, isRequired) {


    override fun getValue(any: Any?, property: KProperty<*>): CommandExecution<T>.() -> String? {
        return { getArgString() }
    }


}

fun <T> ArgumentsContainer<T>.stringArg(name: String, isRequired: Boolean = true): StringArgument<T> {
    return addArgument(StringArgument(name, isRequired))
}