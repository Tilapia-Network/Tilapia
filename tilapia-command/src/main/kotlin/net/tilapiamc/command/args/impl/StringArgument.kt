package net.tilapiamc.command.args.impl

import net.tilapiamc.command.CommandExecution
import net.tilapiamc.command.NetworkCommand
import net.tilapiamc.command.args.CommandArgument
import kotlin.reflect.KProperty

class StringArgument(name: String, isRequired: Boolean = true): CommandArgument<String>(name, isRequired) {


    override fun getValue(any: Any?, property: KProperty<*>): CommandExecution<*>.() -> String? {
        return { getArgString() }
    }


}

fun NetworkCommand<*>.stringArg(name: String, isRequired: Boolean = true): StringArgument {
    return addArgument(StringArgument(name, isRequired))
}