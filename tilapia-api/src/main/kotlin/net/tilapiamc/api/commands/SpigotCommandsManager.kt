package net.tilapiamc.api.commands

import com.comphenix.packetwrapper.WrapperPlayClientTabComplete
import com.comphenix.packetwrapper.WrapperPlayServerTabComplete
import com.comphenix.protocol.PacketType
import com.mojang.brigadier.suggestion.Suggestion
import net.tilapiamc.api.commands.args.PlayerNotFoundException
import net.tilapiamc.api.events.EventsManager
import net.tilapiamc.api.events.annotation.Subscribe
import net.tilapiamc.api.events.annotation.registerAnnotationBasedListener
import net.tilapiamc.api.events.packet.PacketReceiveEvent
import net.tilapiamc.api.events.packet.PacketSendEvent
import net.tilapiamc.api.player.PlayersManager.getLocalPlayer
import net.tilapiamc.command.*
import net.tilapiamc.command.args.impl.EnumNotFoundException
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
        } catch (e: EnumNotFoundException) {
            if (e.exposeValues) {
                event.player.sendMessage(event.player.getLocalPlayer().getLanguageBundle()[LanguageCommand.COMMAND_ENUM_NOT_FOUND_VALUE_EXPOSED]
                    .format(e.value, "${ChatColor.YELLOW}${e.enumValues.joinToString("${ChatColor.RED}, ${ChatColor.YELLOW}")}"))
            } else {
                event.player.sendMessage(event.player.getLocalPlayer().getLanguageBundle()[LanguageCommand.COMMAND_ENUM_NOT_FOUND].format(e.value))
            }
        } catch (e: PlayerNotFoundException) {
            event.player.sendMessage(event.player.getLocalPlayer().getLanguageBundle()[LanguageCommand.COMMAND_PLAYER_NOT_FOUND].format(e.playerName))
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

            val result = handleTabComplete(event.player, chatMessage.substring(1),
                if (event.player.isOp) event.original.packet.stringArrays.read(0).toList() else arrayListOf() )
            event.original.packet.stringArrays.write(0, result.toTypedArray())
        } catch (e: CommandException) {
            event.player.sendMessage("${ChatColor.RED}${e.message}")
        } catch (e: Throwable) {
            event.player.getLocalPlayer().logger.error("Error while tab completing (\"${chatMessage}\")", e)
            return
        }
    }


}



fun BukkitCommandExecution.requiresPlayer(): Player {
    if (sender is Player) {
        return sender as Player
    }
    throw CommandException("You must be a player to use this command")
}
fun BukkitCommandExecution.requiresPermission(permission: String): Player {
    if (sender is Player) {
        if (!sender.hasPermission(permission)) {
            throw CommandException("You don't have the permission to use this command!")
        }
        return sender as Player
    }
    throw CommandException("You must be a player to use this command")
}

typealias BukkitCommand = NetworkCommand<CommandSender>
typealias BukkitCommandExecution = CommandExecution<CommandSender>