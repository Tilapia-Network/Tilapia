package net.tilapiamc.proxyapi.command

import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.event.command.CommandExecuteEvent
import com.velocitypowered.api.event.player.TabCompleteEvent
import com.velocitypowered.api.proxy.Player
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.tilapiamc.command.*
import net.tilapiamc.command.args.impl.EnumNotFoundException
import net.tilapiamc.common.events.annotation.Subscribe
import net.tilapiamc.common.events.annotation.registerAnnotationBasedListener
import net.tilapiamc.common.language.LanguageBundle
import net.tilapiamc.common.language.LanguageKey
import net.tilapiamc.proxyapi.JoinDeniedException
import net.tilapiamc.proxyapi.TilapiaProxyAPI
import net.tilapiamc.proxyapi.command.args.GameNotFoundException
import net.tilapiamc.proxyapi.command.args.PlayerNotFoundException
import net.tilapiamc.proxyapi.permission.PermissionManager
import net.tilapiamc.proxyapi.player.PlayersManager.Companion.getLocalPlayer
import org.apache.logging.log4j.LogManager
import java.util.*

class ProxyCommandsManager(val proxyApi: TilapiaProxyAPI): CommandsManager<CommandSource>(LogManager.getLogger("CommandsManager")) {

    init {
        proxyApi.eventsManager.registerAnnotationBasedListener(this)
    }



    @Subscribe("proxyCommandsManager-handleCommand")
    fun handleCommand(event: CommandExecuteEvent) {
        try {
            event.result = CommandExecuteEvent.CommandResult.denied()
            if (handleCommand(event.commandSource, event.command)) {
            } else {
                event.result = CommandExecuteEvent.CommandResult.forwardToServer()
            }
        } catch (e: EnumNotFoundException) {
            if (e.exposeValues) {
                event.commandSource.sendMessage(event.commandSource.getSenderLanguageBundle()[LanguageCommand.COMMAND_ENUM_NOT_FOUND_VALUE_EXPOSED]
                    .format(e.value, e.enumValues.joinToString(", ")))
            } else {
                event.commandSource.sendMessage(event.commandSource.getSenderLanguageBundle()[LanguageCommand.COMMAND_ENUM_NOT_FOUND].format(e.value))
            }
        } catch (e: GameNotFoundException) {
            event.commandSource.sendMessage(event.commandSource.getSenderLanguageBundle()[LanguageCommand.COMMAND_GAME_NOT_FOUND].format(e.gameId, e.gameId))
        } catch (e: JoinDeniedException) {
            event.commandSource.sendMessage(event.commandSource.getSenderLanguageBundle()[LanguageCommand.JOIN_DENIED].format(e.gameId, e.reason))
        } catch (e: PlayerNotFoundException) {
            event.commandSource.sendMessage(event.commandSource.getSenderLanguageBundle()[LanguageCommand.COMMAND_PLAYER_NOT_FOUND].format(e.playerName))
        } catch (e: UsageException) {
            event.commandSource.sendMessage(event.commandSource.getSenderLanguageBundle()[LanguageCommand.COMMAND_INVALID_USAGE].format(e.message))
        } catch (e: CommandException) {
            event.commandSource.sendMessage("&c${e.message}")
        } catch (e: Throwable) {
            logger.error("Error while handling command (\"${event.command}\"  /  ${event.commandSource})", e)
            event.commandSource.sendMessage(event.commandSource.getSenderLanguageBundle()[LanguageCommand.COMMAND_ERROR])
            return
        }
    }
    @Subscribe("proxyCommandsManager-handleTabComplete")
    fun handleTabComplete(event: TabCompleteEvent) {
        if (!event.partialMessage.startsWith("/")) return
        val result = handleTabComplete(event.player, event.partialMessage.substring(1), event.suggestions)
        event.suggestions.clear()
        event.suggestions.addAll(result)
    }

}

fun CommandSource.sendMessage(message: String) = sendMessage(message.toComponent())
fun String.toComponent(): Component = LegacyComponentSerializer.legacyAmpersand().deserialize(this)

fun CommandSource.getSenderLanguageBundle(): LanguageBundle {
    if (this is Player) {
        return this.getLocalPlayer().getLanguageBundle()
    }
    return TilapiaProxyAPI.instance.languageManager.getLanguageBundle(java.util.Locale.TRADITIONAL_CHINESE)
}
fun ProxyCommandExecution.getLanguageBundle(): LanguageBundle {
    return sender.getSenderLanguageBundle()
}
fun ProxyCommandExecution.requiresPlayer(): Player {
    if (sender is Player) {
        return sender as Player
    }
    throw CommandException("You must be a player to use this command")
}
fun ProxyCommandExecution.requiresPermission(permission: String): Player {
    if (sender is Player) {
        if (!sender.hasPermission(permission)) {
            throw CommandException("You don't have the permission to use this command!")
        }
        return sender as Player
    }
    throw CommandException("You must be a player to use this command")
}

open class ProxySubCommand(parent: ProxyCommand, depth: Int, name: String, val descriptionKey: LanguageKey)
    : NetworkSubCommand<CommandSource>(parent.name, parent, depth, name, descriptionKey.defaultValue) {


    init {
        TilapiaProxyAPI.instance.languageManager.registerLanguageKey(descriptionKey)
    }
    fun getDescription(languageBundle: LanguageBundle): String {
        return languageBundle[descriptionKey]
    }
}

abstract class ProxyCommand(name: String, val descriptionKey: LanguageKey, val requiresPermission: Boolean): NetworkCommand<CommandSource, ProxySubCommand>({ parent, depth, name, description ->
    ProxySubCommand(parent as ProxyCommand, depth, name, LanguageKey("COMMAND_${parent.name.replace("-", "_").uppercase()}_SUB_COMMAND_${name.replace("-", "_").uppercase()}_DESCRIPTION", description))
}, name) {
    val permission: String?

    constructor(name: String, description: String, requiresPermission: Boolean):
            this(name, LanguageKey("COMMAND_${name.replace("-", "_").uppercase()}_DESCRIPTION", description), requiresPermission)
    init {
        if (requiresPermission) {
            permission = PermissionManager.registerCommandUsePermission(name)
        } else {
            permission = null
        }
        TilapiaProxyAPI.instance.languageManager.registerLanguageKey(descriptionKey)
    }
    fun getDescription(languageBundle: LanguageBundle): String {
        return languageBundle[descriptionKey]
    }

    override fun matches(commandName: String, sender: CommandSource): Boolean {
        return super.matches(commandName, sender) && (if (requiresPermission) sender.hasPermission(permission) else true)
    }
}

typealias ProxyCommandExecution = CommandExecution<CommandSource>

fun ProxySubCommand.getCommandLanguageKey(name: String, defaultValue: String): LanguageKey {
    val key = LanguageKey("COMMAND_${parent.name.replace("-", "_").uppercase()}_SUB_COMMAND_${name.replace("-", "_").uppercase()}_$name", defaultValue)
    TilapiaProxyAPI.instance.languageManager.registerLanguageKey(key)
    return key
}
fun ProxyCommand.getCommandLanguageKey(name: String, defaultValue: String): LanguageKey {
    val key = LanguageKey("COMMAND_${name.replace("-", "_").uppercase()}_$name", defaultValue)
    TilapiaProxyAPI.instance.languageManager.registerLanguageKey(key)
    return key
}
