package net.tilapiamc.command

import net.tilapiamc.command.args.CommandArgument
import java.lang.RuntimeException

abstract class NetworkCommand<T>(name: String, description: String): AbstractCommand<T>(name, description) {

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


    override fun execute(commandAlias: String, sender: T, args: Array<String>) {
        val parsed = parseArgs(args)
        val subCommand = subCommands.firstOrNull { it.matches(parsed[0], sender) }
        if (subCommand != null) {
            subCommand.execute(commandAlias, sender, args)
        } else {
            onCommand(CommandExecution(this, sender, commandAlias, args, parseArgs(args)))
        }
    }

    override fun tabComplete(commandAlias: String, sender: T, args: Array<String>): Collection<String> {
        val parsed = parseArgs(args)
        val currentArgumentIndex = parsed.size - 1
        if (currentArgumentIndex >= this.args.size) {
            return listOf()
        }
        if (subCommands.isNotEmpty()) {
            if (currentArgumentIndex <= 1) {
                val commandsList = ArrayList<String>()
                commandsList.addAll(subCommands.filter { it.canUseCommandAction(sender) }.map { it.name })
                for (strings in subCommands.filter { it.canUseCommandAction(sender) }.map { it.aliases }) {
                    commandsList.addAll(strings.map { it })
                }
                return commandsList.filter { it.lowercase().startsWith(args.first().lowercase()) }
            }
            val subCommand = subCommands.firstOrNull { it.matches(parsed[0], sender) }
            return subCommand?.tabComplete(commandAlias, sender, args)?: arrayListOf()
        }
        val targetArgument = this.args[currentArgumentIndex]
        return targetArgument.tabComplete(parsed[currentArgumentIndex])
    }

    val args = ArrayList<CommandArgument<*>>()

    private var canUseCommand: T.() -> Boolean = { true }

    fun canUseCommand(action: T.() -> Boolean) {
        this.canUseCommand = action
    }
    private var onCommand: CommandExecution<T>.() -> Boolean = { false }
    fun onCommand(action: CommandExecution<T>.() -> Boolean) {
        this.onCommand = action
    }

    fun <T: CommandArgument<*>> addArgument(arg: T): T {
        if (arg.isRequired && args.any { !it.isRequired }) {
            throw IllegalArgumentException("Argument: ${arg.name} is required but there are already optional arguments")
        }
        arg.index = args.size
        args.add(arg)
        return arg
    }

    override fun getUsageString(): String {
        return ""
    }
    val subCommands: List<NetworkSubCommand<T>> = ArrayList()

    fun subCommand(name: String, description: String, action: NetworkSubCommand<T>.() -> Unit) {
        val subCommand = NetworkSubCommand<T>(this, 0, name, description)
        subCommand.action()
        (subCommands as MutableList).add(subCommand)
    }

}

class CommandExecution<T>(val command: NetworkCommand<T>, val sender: T, val commandAlias: String, val rawArgs: Array<String>, val parsedArgs: Array<String>) {
    fun commandError(message: String): Nothing {
        throw CommandException(message)
    }
    fun invalidUsage(): Nothing {
        throw UsageException("/${command.name} ${command.getUsageString()}")
    }
}
open class CommandException(message: String): RuntimeException(message)
class UsageException(message: String): CommandException(message)