package net.tilapiamc.core.commands

import net.tilapiamc.command.NetworkCommand
import net.tilapiamc.command.args.impl.stringArg
import org.bukkit.command.CommandSender

class CommandTest: NetworkCommand<CommandSender>("test", "A command for testing") {

    init {
        addAlias("t")
        val message by stringArg("message")
        onCommand {
            sender.sendMessage("Hello, ${message()}!")
            true
        }
    }

}