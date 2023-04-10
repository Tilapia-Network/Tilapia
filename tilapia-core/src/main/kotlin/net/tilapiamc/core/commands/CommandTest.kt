package net.tilapiamc.core.commands

import net.tilapiamc.command.NetworkCommand
import net.tilapiamc.command.args.impl.stringArg
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandTest: NetworkCommand<CommandSender>("test", "A command for testing") {

    companion object {
        const val PERMISSION = "commands.test"
    }

    init {
        addAlias("t")
        val message by stringArg("message")
        onCommand {
            sender.sendMessage("Hello, ${message()}!")
            true
        }
        canUseCommand {
            hasPermission(CommandLobbyLocal.PERMISSION) && this is Player
        }
    }

}