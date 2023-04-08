package net.tilapia.core.commands

import net.tilapia.api.commands.NetworkCommand
import net.tilapia.api.commands.args.impl.stringArg

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