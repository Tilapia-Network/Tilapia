package net.tilapiamc.api.commands

import com.comphenix.packetwrapper.WrapperPlayClientTabComplete
import com.comphenix.packetwrapper.WrapperPlayServerTabComplete
import com.comphenix.protocol.PacketType
import com.mojang.brigadier.suggestion.Suggestion
import net.tilapiamc.api.events.EventsManager
import net.tilapiamc.api.events.annotation.Subscribe
import net.tilapiamc.api.events.annotation.registerAnnotationBasedListener
import net.tilapiamc.api.events.packet.PacketReceiveEvent
import net.tilapiamc.api.events.packet.PacketSendEvent
import net.tilapiamc.api.player.PlayersManager.getLocalPlayer
import net.tilapiamc.command.CommandException
import net.tilapiamc.command.CommandExecution
import net.tilapiamc.command.CommandsManager
import net.tilapiamc.command.UsageException
import org.apache.logging.log4j.LogManager
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerChatTabCompleteEvent
import org.bukkit.event.player.PlayerCommandPreprocessEvent

object SpigotCommandsManager: CommandsManager<CommandSender>(LogManager.getLogger("CommandsManager")) {


    init {
        EventsManager.registerAnnotationBasedListener(this)
    }


    @Subscribe("commandsManagerExecute")
    fun onCommand(event: PlayerCommandPreprocessEvent) {
        fun unknownCommand() {
            event.player.sendMessage(event.player.getLocalPlayer().getLanguageBundle()[LanguageCommand.COMMAND_NOT_FOUND])
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
        } catch (e: UsageException) {
            event.player.sendMessage(event.player.getLocalPlayer().getLanguageBundle()[LanguageCommand.COMMAND_INVALID_USAGE].format(e.message))
        } catch (e: CommandException) {
            event.player.sendMessage("${ChatColor.RED}${e.message}")
        } catch (e: Throwable) {
            event.player.getLocalPlayer().logger.error("Error while handling command (\"${event.message}\")", e)
            event.player.sendMessage(event.player.getLocalPlayer().getLanguageBundle()[LanguageCommand.COMMAND_ERROR])
            return
        }
    }

    val tabCompletions = HashMap<Player, String>()

    @Subscribe("commandsManagerTabCompleteListen")
    fun tabCompleteListen(event: PacketReceiveEvent) {
        if (event.original.packetType != PacketType.Play.Client.TAB_COMPLETE) return
        tabCompletions[event.player] = event.original.packet.strings.read(0)
    }

    @Subscribe("commandsManagerTabComplete")
    fun onTabComplete(event: PacketSendEvent) {
        if (event.original.packetType != PacketType.Play.Server.TAB_COMPLETE) return
        val chatMessage = tabCompletions[event.player]?:""
        if (!chatMessage.startsWith("/")) return
        try {
            val result = handleTabComplete(event.player, chatMessage.substring(1))
            val out = ArrayList<String>()
            out.addAll(event.original.packet.stringArrays.read(0))
            if (!event.player.isOp) {
                out.clear()
            }
            out.addAll(result)
            event.original.packet.stringArrays.write(0, out.toTypedArray())
        } catch (e: CommandException) {
            event.player.sendMessage("${ChatColor.RED}${e.message}")
        } catch (e: Throwable) {
            event.player.getLocalPlayer().logger.error("Error while tab completing (\"${chatMessage}\")", e)
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