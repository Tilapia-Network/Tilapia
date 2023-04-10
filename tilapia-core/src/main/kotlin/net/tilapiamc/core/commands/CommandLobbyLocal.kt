package net.tilapiamc.core.commands

import net.tilapiamc.api.TilapiaCore
import net.tilapiamc.api.commands.requiresPlayer
import net.tilapiamc.api.player.PlayersManager.getLocalPlayer
import net.tilapiamc.command.NetworkCommand
import net.tilapiamc.command.args.impl.stringArg
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandLobbyLocal: NetworkCommand<CommandSender>("lobby-local", "[Dev] Join the main lobby") {

    companion object {
        const val PERMISSION = "commands.lobby-local"
    }

    init {
        canUseCommand {
            hasPermission(PERMISSION) && this is Player
        }
        onCommand {
            val core = TilapiaCore.instance
            val player = requiresPlayer()
            val localPlayer = player.getLocalPlayer()

            val game = localPlayer.findLobbyToJoin("main")
                ?: commandError("Could not find any lobby to join")

            sender.sendMessage("${ChatColor.GRAY}Sending you to a main lobby\n")
            localPlayer.sendToGame(game)

            true
        }
    }

}