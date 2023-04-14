package net.tilapiamc.multiworld.args

import net.tilapiamc.api.generators.AbstractGenerator
import net.tilapiamc.api.generators.Generators
import net.tilapiamc.command.ArgumentsContainer
import net.tilapiamc.command.CommandExecution
import net.tilapiamc.command.args.CommandArgument
import net.tilapiamc.multiworld.GeneratorNotFoundException
import kotlin.reflect.KProperty

class GeneratorArgument<S>(name: String, isRequired: Boolean = false): CommandArgument<AbstractGenerator, S>(name, isRequired) {

    override fun getValue(any: Any?, property: KProperty<*>): CommandExecution<S>.() -> AbstractGenerator? {
        return lambda@{
            val arg = getArgString()?:return@lambda null
            val split = arg.split(":")
            val generatorName = split[0]
            val generatorOption = if (split.size > 1) split[1] else null
            val generator = Generators.generators[generatorName]?:throw GeneratorNotFoundException(generatorName)
            generator(generatorOption)
        }
    }

    override fun tabComplete(sender: S, token: String): Collection<String> {
        return Generators.generators.keys.filter { it.lowercase().startsWith(token) }
    }
}


fun <T> ArgumentsContainer<T>.generatorArg(name: String, isRequired: Boolean = true): GeneratorArgument<T> {
    return addArgument(GeneratorArgument(name, isRequired))
}
