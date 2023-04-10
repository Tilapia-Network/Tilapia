package net.tilapiamc.api.commands

import net.tilapiamc.api.events.EventsManager
import net.tilapiamc.api.events.annotation.Subscribe
import net.tilapiamc.api.events.annotation.registerAnnotationBasedListener
import net.tilapiamc.api.player.PlayersManager.getLocalPlayer
import net.tilapiamc.command.CommandException
import net.tilapiamc.command.CommandExecution
import net.tilapiamc.command.CommandsManager
import org.apache.logging.log4j.LogManager
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerCommandPreprocessEvent

object SpigotCommandsManager: CommandsManager<CommandSender>(LogManager.getLogger("CommandsManager")) {


    init {
        EventsManager.registerAnnotationBasedListener(this)
    }


    @Subscribe("commandsManagerExecute")
    fun onCommand(event: PlayerCommandPreprocessEvent) {
        fun unknownCommand() {
            event.player.sendMessage("${ChatColor.RED}Unknown command! Please refer to our documentation for full commands list.")
        }
        event.isCancelled = true
        try {
            if (handleCommand(event.player, event.message.substring(1))) {
            } else {
                if (event.player.isOp) {
                    event.isCancelled = false
                } else {
                    unknownCommand()
                }
            }
        } catch (e: CommandException) {
            event.player.sendMessage("${ChatColor.RED}${e.message}")
        } catch (e: Throwable) {
            event.player.getLocalPlayer().logger.error("Error while handling command (\"${event.message}\")", e)
            event.player.sendMessage("${ChatColor.RED}Error while handling the command! Please contact server administrator.")
            return
        }

    }


}



fun CommandExecution<CommandSender>.requiresPlayer(): Player {
    if (sender is Player) {
        return sender as Player
    }
    throw CommandException("You must be a player to use this command")
}
fun CommandExecution<CommandSender>.requiresPermission(permission: String): Player {
    if (sender is Player) {
        if (!sender.hasPermission(permission)) {
            throw CommandException("You don't have the permission to use this command!")
        }
        return sender as Player
    }
    throw CommandException("You must be a player to use this command")
}