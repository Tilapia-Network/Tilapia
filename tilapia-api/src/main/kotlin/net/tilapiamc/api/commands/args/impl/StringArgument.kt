package net.tilapiamc.api.commands.args.impl

import net.tilapiamc.api.commands.CommandExecution
import net.tilapiamc.api.commands.NetworkCommand
import net.tilapiamc.api.commands.args.CommandArgument
import kotlin.reflect.KProperty

class StringArgument(name: String, isRequired: Boolean = true): CommandArgument<String>(name, isRequired) {


    override fun getValue(any: Any?, property: KProperty<*>): CommandExecution.() -> String? {
        return { getArgString() }
    }


}

fun NetworkCommand.stringArg(name: String, isRequired: Boolean = true): StringArgument {
    return addArgument(StringArgument(name, isRequired))
}