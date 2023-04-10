package net.tilapiamc.core.commands

import net.tilapiamc.api.TilapiaCore
import net.tilapiamc.api.commands.requiresPlayer
import net.tilapiamc.api.player.PlayersManager.getLocalPlayer
import net.tilapiamc.command.NetworkCommand
import net.tilapiamc.command.args.impl.stringArg
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandJoinLocal: NetworkCommand<CommandSender>("join-local", "[Dev] Join a local game") {

    companion object {
        const val PERMISSION = "commands.join-local"
    }

    init {
        val miniGameType by stringArg("GameType")
        canUseCommand {
            hasPermission(PERMISSION) && this is Player
        }
        onCommand {
            val core = TilapiaCore.instance
            val player = requiresPlayer()
            val localPlayer = player.getLocalPlayer()

            val game = localPlayer.findMiniGameToJoin(miniGameType()!!)
                ?: commandError("Could not find any game matching \"${miniGameType()}\"")

            sender.sendMessage("${ChatColor.GRAY}Sending you to a game of ${miniGameType()}\n")
            localPlayer.sendToGame(game)

            true
        }
    }

}