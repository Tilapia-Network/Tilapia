package net.tilapiamc.core.commands

import net.tilapiamc.api.TilapiaCore
import net.tilapiamc.api.commands.BukkitCommand
import net.tilapiamc.api.commands.requiresPlayer
import net.tilapiamc.api.player.PlayersManager.getLocalPlayer
import net.tilapiamc.language.LanguageCore
import org.bukkit.ChatColor
import org.bukkit.entity.Player

class CommandLobbyLocal: BukkitCommand("lobby-local", "[開發者] 加入一個伺服器的主要大廳", true) {

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
                ?: commandError(localPlayer.getLanguageBundle()[LanguageCore.COULD_NOT_FIND_LOBBY])

            sender.sendMessage("${ChatColor.GRAY}Sending you to a main lobby\n")
            localPlayer.sendToGame(game)

            true
        }
    }

}