package net.tilapiamc.command

import net.tilapiamc.command.args.CommandArgument

open class NetworkSubCommand<T>(val usagePrefix: String, val parent: NetworkCommand<T, *>, val depth: Int = 0, val name: String, val description: String): ArgumentsContainer<T> {
    override val args = ArrayList<CommandArgument<*, T>>()


    val aliases = ArrayList<String>()

    fun addAlias(vararg alias: String) {
        aliases.addAll(alias)
    }

    fun matches(commandName: String, sender: T): Boolean {
        return if (commandName == name || commandName in aliases) canUseCommandAction(sender) else false
    }

    fun execute(commandAlias: String, sender: T, args: Array<String>) {
        val parsed = NetworkCommand.parseArgs(args).toList().let {
            it.subList(depth + 1, it.size)
        }
        val subCommand = subCommands.firstOrNull { it.matches(parsed[0], sender) }
        if (subCommand != null) {
            subCommand.execute(commandAlias, sender, args)
        } else {
            onCommandAction(CommandExecution(parent, "/$usagePrefix $name ${getUsageString()}", sender, commandAlias, args, parsed.toTypedArray()))
        }
    }

    fun tabComplete(commandAlias: String, sender: T, args: Array<String>): Collection<String> {
        val parsed = NetworkCommand.parseArgs(args).toList().let {
            it.subList(depth + 1, it.size)
        }
        val currentArgumentIndex = (parsed.size - 1)
        if (currentArgumentIndex >= this.args.size) {
            return listOf()
        }
        if (subCommands.isNotEmpty()) {
            if (currentArgumentIndex <= 0) {
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
        return targetArgument.tabComplete(sender, parsed[currentArgumentIndex])
    }


    var canUseCommandAction: T.() -> Boolean = { true }

    fun canUseCommand(action: T.() -> Boolean) {
        this.canUseCommandAction = action
    }
    var onCommandAction: CommandExecution<T>.() -> Boolean = { false }
    fun onCommand(action: CommandExecution<T>.() -> Boolean) {
        this.onCommandAction = action
    }


    fun getUsageString(): String {
        return args.joinToString(" ")
    }

    val subCommands = ArrayList<NetworkSubCommand<T>>()
    fun subCommand(name: String, description: String, action: NetworkSubCommand<T>.() -> Unit) {
        val subCommand = NetworkSubCommand<T>("$usagePrefix $name", parent, depth + 1, name, description)
        subCommand.action()
        subCommands.add(subCommand)
    }


}