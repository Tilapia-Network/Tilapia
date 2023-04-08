package net.tilapia.api.commands

import net.tilapia.api.events.EventsManager
import net.tilapia.api.events.annotation.Subscribe
import net.tilapia.api.events.annotation.registerAnnotationBasedListener
import net.tilapia.api.player.PlayersManager.getNetworkPlayer
import org.apache.logging.log4j.LogManager
import org.bukkit.ChatColor
import org.bukkit.event.player.PlayerCommandPreprocessEvent

object CommandsManager {

    val commands = ArrayList<AbstractCommand>()
    val logger = LogManager.getLogger("CommandsManager")

    init {
        EventsManager.registerAnnotationBasedListener(this)
    }


    fun registerCommand(command: AbstractCommand) {
        logger.debug("Registered command: ${command.name}")
        commands.add(command)
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
                event.player.getNetworkPlayer().logger.warn("Player has executed empty command")
            }
            val commandName = split[0].substring(1)
            val args = split.subList(1, split.size)
            val command = commands.firstOrNull { it.matches(commandName, event.player) }
            if (command == null) {
                if (event.player.isOp) {
                    event.isCancelled = false
                    return
                }
                unknownCommand()
                return
            }

            command.execute(commandName, event.player.getNetworkPlayer(), args.toTypedArray())
        } catch (e: Throwable) {
            logger.error(e.stackTraceToString())
            event.player.sendMessage("${ChatColor.RED}Something went wrong while processing the command! Please report it to server administrator.")
            event.isCancelled = true
        }

    }


}