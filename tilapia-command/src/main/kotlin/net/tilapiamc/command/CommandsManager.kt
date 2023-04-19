package net.tilapiamc.command

import org.apache.logging.log4j.Logger

open class CommandsManager<T>(val logger: Logger) {

    val commands = ArrayList<AbstractCommand<T>>()


    open fun registerCommand(command: AbstractCommand<T>) {
        logger.debug("Registered command: ${command.name}")
        logger.warn("Tilapia command API has been deprecated, as it's switching to use brigadier")
        commands.add(command)
    }
    open fun unregisterCommand(command: AbstractCommand<T>) {
        logger.debug("Unregistered command: ${command.name}")
        commands.remove(command)
    }

    fun handleCommand(sender: T, commandIn: String): Boolean {
        val split = commandIn.split(" ")
        if (split.isEmpty()) {
            return false
        }
        val commandName = split[0]
        val args = split.subList(1, split.size)
        val command = commands.firstOrNull { it.matches(commandName, sender) } ?: return false
        command.execute(commandName, sender, args.toTypedArray())
        return true
    }
    fun handleTabComplete(sender: T, commandIn: String, original: Collection<String>): Collection<String> {
        val split = commandIn.split(" ")
        val commandsList = ArrayList<String>()
        commandsList.addAll(commands.filter { it.matches(it.name, sender) }.map { "/" + it.name })
        commandsList.addAll(original.filter { it !in commandsList })
        for (strings in commands.filter { it.matches(it.name, sender) }.map { it.aliases }) {
            commandsList.addAll(strings.map { "/$it" })
        }
        if (split.isEmpty()) {
            return commandsList
        }
        val commandName = split[0]
        val args = split.subList(1, split.size)
        if (split.size <= 1) {
            return commandsList.filter { it.startsWith("/$commandName") }
        }
        val command = commands.firstOrNull { it.matches(commandName, sender) } ?: return original
        return command.tabComplete(commandName, sender, args.toTypedArray())
    }



}