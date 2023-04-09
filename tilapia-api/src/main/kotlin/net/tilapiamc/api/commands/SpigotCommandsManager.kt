package net.tilapiamc.api.commands

import net.tilapiamc.api.events.EventsManager
import net.tilapiamc.api.events.annotation.Subscribe
import net.tilapiamc.api.events.annotation.registerAnnotationBasedListener
import net.tilapiamc.api.player.PlayersManager.getLocalPlayer
import net.tilapiamc.command.CommandException
import net.tilapiamc.command.CommandsManager
import org.apache.logging.log4j.LogManager
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
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
        try {
            if (handleCommand(event.player, event.message.substring(1))) {
                event.isCancelled = true
            } else {
                if (event.player.isOp) {
                } else {
                    event.isCancelled = true
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