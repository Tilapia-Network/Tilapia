package net.tilapiamc.api.commands

import net.tilapiamc.api.commands.args.CommandArgument
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import java.lang.RuntimeException

abstract class NetworkCommand(name: String, description: String): net.tilapiamc.api.commands.AbstractCommand(name, description) {

    val aliases = ArrayList<String>()

    fun addAlias(vararg alias: String) {
        aliases.addAll(alias)
    }

    override fun matches(commandName: String, sender: CommandSender): Boolean {
        return if (commandName == name || commandName in aliases) canUseCommand(sender) else false
    }


    open fun canUseCommand(sender: CommandSender): Boolean {
        return true
    }

    override fun execute(commandAlias: String, sender: CommandSender, args: Array<String>) {
        try {
            onCommand(CommandExecution(this, sender, commandAlias, args, args))
        } catch (e: CommandException) {
            sender.sendMessage("${ChatColor.RED}${e.message}")
        }
    }

    override fun tabComplete(commandAlias: String, sender: CommandSender, args: Array<String>): Array<String> {
        return arrayOf()
    }

    val args = ArrayList<CommandArgument<*>>()

    private var onCommand: CommandExecution.() -> Boolean = { false }
    fun onCommand(action: CommandExecution.() -> Boolean) {
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

class CommandExecution(val command: NetworkCommand, val sender: CommandSender, val commandAlias: String, val rawArgs: Array<String>, val parsedArgs: Array<String>) {
    fun commandError(message: String): Nothing {
        throw CommandException(message)
    }
    fun invalidUsage(): Nothing {
        throw CommandException("Invalid usage! Usage: /${command.name} ${command.getUsageString()}")
    }
}
class CommandException(message: String): RuntimeException(message)