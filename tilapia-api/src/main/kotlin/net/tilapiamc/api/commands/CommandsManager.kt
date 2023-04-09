package net.tilapiamc.api.commands

import net.tilapiamc.api.events.EventsManager
import net.tilapiamc.api.events.annotation.Subscribe
import net.tilapiamc.api.events.annotation.registerAnnotationBasedListener
import net.tilapiamc.api.player.PlayersManager.getLocalPlayer
import org.apache.logging.log4j.LogManager
import org.bukkit.ChatColor
import org.bukkit.event.player.PlayerCommandPreprocessEvent

object CommandsManager {

    val commands = ArrayList<net.tilapiamc.api.commands.AbstractCommand>()
    val logger = LogManager.getLogger("CommandsManager")

    init {
        EventsManager.registerAnnotationBasedListener(this)
    }


    fun registerCommand(command: net.tilapiamc.api.commands.AbstractCommand) {
        net.tilapiamc.api.commands.CommandsManager.logger.debug("Registered command: ${command.name}")
        net.tilapiamc.api.commands.CommandsManager.commands.add(command)
    }

    @Subscribe("commandsManagerExecute")
    fun onCommand(event: PlayerCommandPreprocessEvent) {
        try {
            event.isCancelled = true
            fun unknownCommand() {
                event.player.sendMessage("${ChatColor.RED}Unknown command! Please refer to our documentation for full commands list.")
            }
            val split = event.message.split(" ")
            if (split.isEmpty()) {
                unknownCommand()
                event.player.getLocalPlayer().logger.warn("Player has executed empty command")
            }
            val commandName = split[0].substring(1)
            val args = split.subList(1, split.size)
            val command = net.tilapiamc.api.commands.CommandsManager.commands.firstOrNull { it.matches(commandName, event.player) }
            if (command == null) {
                if (event.player.isOp) {
                    event.isCancelled = false
                    return
                }
                unknownCommand()
                return
            }

            command.execute(commandName, event.player.getLocalPlayer(), args.toTypedArray())
        } catch (e: Throwable) {
            net.tilapiamc.api.commands.CommandsManager.logger.error(e.stackTraceToString())
            event.player.sendMessage("${ChatColor.RED}Something went wrong while processing the command! Please report it to server administrator.")
            event.isCancelled = true
        }

    }


}