package net.tilapiamc.command.args

import net.tilapiamc.command.CommandExecution
import kotlin.reflect.KProperty

abstract class CommandArgument<T>(val name: String, val isRequired: Boolean = true) {
    abstract operator fun getValue(any: Any?, property: KProperty<*>): CommandExecution<*>.() -> T?

    var index = 0

    fun CommandExecution<*>.getArgString(): String? {
        if (parsedArgs.size > index) {
            return parsedArgs[index]
        }
        if (isRequired) {
            invalidUsage()
        }
        return if (parsedArgs.size > index) parsedArgs[index] else null
    }

}