package net.tilapia.api.commands.args.impl

import net.tilapia.api.commands.CommandExecution
import net.tilapia.api.commands.NetworkCommand
import net.tilapia.api.commands.args.CommandArgument
import kotlin.reflect.KProperty

class StringArgument(name: String, isRequired: Boolean = true): CommandArgument<String>(name, isRequired) {


    override fun getValue(any: Any?, property: KProperty<*>): CommandExecution.() -> String? {
        return { getArgString() }
    }


}

fun NetworkCommand.stringArg(name: String, isRequired: Boolean = true): StringArgument {
    return addArgument(StringArgument(name, isRequired))
}