package net.tilapiamc.command

import net.tilapiamc.command.args.CommandArgument
import java.lang.RuntimeException

abstract class NetworkCommand<T>(name: String, description: String): AbstractCommand<T>(name, description) {

    override val aliases = ArrayList<String>()

    fun addAlias(vararg alias: String) {
        aliases.addAll(alias)
    }

    override fun matches(commandName: String, sender: T): Boolean {
        return if (commandName == name || commandName in aliases) canUseCommand(sender) else false
    }

    fun parseArgs(input: Array<String>): Array<String> {
        return input
    }

    override fun execute(commandAlias: String, sender: T, args: Array<String>) {
        onCommand(CommandExecution(this, sender, commandAlias, args, parseArgs(args)))
    }

    override fun tabComplete(commandAlias: String, sender: T, args: Array<String>): Collection<String> {
        val parsed = parseArgs(args)
        val currentArgumentIndex = (parsed.size - 1).coerceAtLeast(0)
        if (currentArgumentIndex >= this.args.size) {
            return listOf()
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


}

class CommandExecution<T>(val command: NetworkCommand<T>, val sender: T, val commandAlias: String, val rawArgs: Array<String>, val parsedArgs: Array<String>) {
    fun commandError(message: String): Nothing {
        throw CommandException(message)
    }
    fun invalidUsage(): Nothing {
        throw CommandException("Invalid usage! Usage: /${command.name} ${command.getUsageString()}")
    }
}
class CommandException(message: String): RuntimeException(message)