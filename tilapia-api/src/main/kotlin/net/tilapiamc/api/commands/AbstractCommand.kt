package net.tilapiamc.api.commands

import org.bukkit.command.CommandSender

abstract class AbstractCommand(val name: String, val description: String) {

    abstract fun matches(commandName: String, sender: CommandSender): Boolean
    abstract fun execute(commandAlias: String, sender: CommandSender, args: Array<String>)
    abstract fun tabComplete(commandAlias: String, sender: CommandSender, args: Array<String>): Array<String>
    abstract fun getUsageString(): String

}