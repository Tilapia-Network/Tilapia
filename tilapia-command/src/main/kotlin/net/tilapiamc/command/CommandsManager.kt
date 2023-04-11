package net.tilapiamc.command

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

open class CommandsManager<T>(val logger: Logger) {

    val commands = ArrayList<AbstractCommand<T>>()



    fun registerCommand(command: AbstractCommand<T>) {
        logger.debug("Registered command: ${command.name}")
        commands.add(command)
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
        val command = commands.firstOrNull { it.matches(commandName, sender) } ?: return arrayListOf()
        return command.tabComplete(commandName, sender, args.toTypedArray())
    }



}