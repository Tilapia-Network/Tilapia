package net.tilapiamc.core.commands

import net.tilapiamc.api.commands.NetworkCommand
import net.tilapiamc.api.commands.args.impl.stringArg

class CommandTest: NetworkCommand("test", "A command for testing") {

    init {
        addAlias("t")
        val message by stringArg("message")
        onCommand {
            sender.sendMessage("Hello, ${message()}!")
            true
        }
    }

}