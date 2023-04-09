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



}