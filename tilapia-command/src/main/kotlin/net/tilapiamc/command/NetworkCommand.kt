package net.tilapiamc.command

import net.tilapiamc.command.args.CommandArgument
import java.lang.RuntimeException

abstract class NetworkCommand<T, S: NetworkSubCommand<T>>(
    val subCommandFactory: (parent: NetworkCommand<T, S>, depth: Int, name: String, description: String) -> S,
    name: String): ArgumentsContainer<T>, AbstractCommand<T>(name) {
    override val args = ArrayList<CommandArgument<*, T>>()

    companion object {
        fun parseArgs(input: Array<String>): Array<String> {
            return input
        }

    }

    override val aliases = ArrayList<String>()

    fun addAlias(vararg alias: String) {
        aliases.addAll(alias)
    }

    override fun matches(commandName: String, sender: T): Boolean {
        return if (commandName == name || commandName in aliases) canUseCommand(sender) else false
    }

    val exceptionHandlers = ArrayList<(e: Throwable, sender: T, args: Array<String>) -> Boolean>()

    override fun execute(commandAlias: String, sender: T, args: Array<String>) {
        try {
            val parsed = parseArgs(args)
            val subCommand = if (parsed.isNotEmpty()) subCommands.firstOrNull { it.matches(parsed[0], sender) } else null
            if (subCommand != null) {
                subCommand.execute(commandAlias, sender, args)
            } else {
                onCommand(CommandExecution(this, "/$name ${getUsageString()}", sender, commandAlias, args, parseArgs(args)))
            }
        } catch (e: Throwable) {
            for (exceptionHandler in exceptionHandlers) {
                if (exceptionHandler(e, sender, args)) {
                    return
                }
            }
            throw e
        }
    }

    override fun tabComplete(commandAlias: String, sender: T, args: Array<String>): Collection<String> {
        val parsed = parseArgs(args)
        val currentArgumentIndex = parsed.size - 1
        if (subCommands.isNotEmpty()) {
            if (currentArgumentIndex <= 0) {
                val commandsList = ArrayList<String>()
                commandsList.addAll(subCommands.filter { it.matches(it.name, sender) }.map { it.name })
                for (strings in subCommands.filter { it.matches(it.name, sender) }.map { it.aliases }) {
                    commandsList.addAll(strings.map { it })
                }
                return commandsList.filter { it.lowercase().startsWith(parsed.first().lowercase()) }
            }
            val subCommand = subCommands.firstOrNull { it.matches(parsed[0], sender) }
            return subCommand?.tabComplete(commandAlias, sender, args)?: arrayListOf()
        }
        if (currentArgumentIndex >= this.args.size) {
            return listOf()
        }
        val targetArgument = this.args[currentArgumentIndex]
        return targetArgument.tabComplete(sender, parsed[currentArgumentIndex])
    }


    private var canUseCommand: T.() -> Boolean = { true }

    fun canUseCommand(action: T.() -> Boolean) {
        this.canUseCommand = action
    }
    private var onCommand: CommandExecution<T>.() -> Boolean = { false }
    fun onCommand(action: CommandExecution<T>.() -> Boolean) {
        this.onCommand = action
    }



    override fun getUsageString(): String {
        return args.joinToString(" ")
    }
    val subCommands: List<S> = ArrayList()

    fun subCommand(name: String, description: String, action: S.() -> Unit) {
        val subCommand = subCommandFactory(this, 0, name, description)
        subCommand.action()
        (subCommands as MutableList).add(subCommand)
    }

}

class CommandExecution<T>(val command: NetworkCommand<T, *>, val usageString: String, val sender: T, val commandAlias: String, val rawArgs: Array<String>, val parsedArgs: Array<String>) {
    fun commandError(message: String): Nothing {
        throw CommandException(message)
    }
    fun invalidUsage(): Nothing {
        throw UsageException(usageString)
    }
}
open class CommandException(message: String): RuntimeException(message)
class UsageException(message: String): CommandException(message)

interface ArgumentsContainer<T> {
    val args: ArrayList<CommandArgument<*, T>>

    fun <A: CommandArgument<*, T>> addArgument(arg: A): A {
        if (arg.isRequired && args.any { !it.isRequired }) {
            throw IllegalArgumentException("Argument: ${arg.name} is required but there are already optional arguments")
        }
        arg.index = args.size
        args.add(arg)
        return arg
    }
}