package net.tilapiamc.api.commands

import com.comphenix.protocol.PacketType
import net.tilapiamc.api.TilapiaCore
import net.tilapiamc.api.commands.args.PlayerNotFoundException
import net.tilapiamc.api.events.EventsManager
import net.tilapiamc.api.events.annotation.Subscribe
import net.tilapiamc.api.events.annotation.registerAnnotationBasedListener
import net.tilapiamc.api.events.packet.PacketReceiveEvent
import net.tilapiamc.api.events.packet.PacketSendEvent
import net.tilapiamc.api.language.LanguageBundle
import net.tilapiamc.api.language.LanguageKey
import net.tilapiamc.api.permission.PermissionManager
import net.tilapiamc.api.player.PlayersManager.getLocalPlayer
import net.tilapiamc.command.*
import net.tilapiamc.command.args.impl.EnumNotFoundException
import org.apache.logging.log4j.LogManager
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.permissions.Permission
import java.util.*
import kotlin.collections.HashMap

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
                    .format(e.value, e.enumValues.joinToString(", ")))
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



fun CommandSender.getSenderLanguageBundle(): LanguageBundle {
    if (this is Player) {
        return (this as Player).getLocalPlayer().getLanguageBundle()
    }
    return TilapiaCore.instance.languageManager.getLanguageBundle(Locale.TRADITIONAL_CHINESE)
}
fun BukkitCommandExecution.getLanguageBundle(): LanguageBundle {
    return sender.getSenderLanguageBundle()
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

open class BukkitSubCommand(parent: BukkitCommand, depth: Int, name: String, val descriptionKey: LanguageKey)
    : NetworkSubCommand<CommandSender>(parent, depth, name, descriptionKey.defaultValue) {


    init {
        TilapiaCore.instance.languageManager.registerLanguageKey(descriptionKey)
    }
    fun getDescription(languageBundle: LanguageBundle): String {
        return languageBundle[descriptionKey]
    }
}

abstract class BukkitCommand(name: String, val descriptionKey: LanguageKey, requiresOp: Boolean = false, val requiresPermission: Boolean = true): NetworkCommand<CommandSender, BukkitSubCommand>({parent, depth, name, description ->
    BukkitSubCommand(parent as BukkitCommand, depth, name, LanguageKey("COMMAND_${parent.name.replace("-", "_").uppercase()}_SUB_COMMAND_${name.replace("-", "_").uppercase()}_DESCRIPTION", description))
}, name) {
    val permission: Permission?

    constructor(name: String, description: String, requiresOp: Boolean = false, requiresPermission: Boolean = true):
            this(name, LanguageKey("COMMAND_${name.replace("-", "_").uppercase()}_DESCRIPTION", description), requiresOp, requiresPermission)
    init {
        if (requiresOp && !requiresPermission) {
            throw IllegalArgumentException("Require Permission must be enabled if requires OP is true")
        }
        if (requiresPermission) {
            permission = PermissionManager.registerCommandUsePermission(name, requiresOp)
        } else {
            permission = null
        }
        TilapiaCore.instance.languageManager.registerLanguageKey(descriptionKey)
    }
    fun getDescription(languageBundle: LanguageBundle): String {
        return languageBundle[descriptionKey]
    }

    override fun matches(commandName: String, sender: CommandSender): Boolean {
        return super.matches(commandName, sender) && (if (requiresPermission) sender.hasPermission(permission) else true)
    }
}

typealias BukkitCommandExecution = CommandExecution<CommandSender>

fun BukkitSubCommand.getCommandLanguageKey(name: String, defaultValue: String): LanguageKey {
    val key = LanguageKey("COMMAND_${parent.name.replace("-", "_").uppercase()}_SUB_COMMAND_${name.replace("-", "_").uppercase()}_$name", defaultValue)
    TilapiaCore.instance.languageManager.registerLanguageKey(key)
    return key
}
fun BukkitCommand.getCommandLanguageKey(name: String, defaultValue: String): LanguageKey {
    val key = LanguageKey("COMMAND_${name.replace("-", "_").uppercase()}_$name", defaultValue)
    TilapiaCore.instance.languageManager.registerLanguageKey(key)
    return key
}
