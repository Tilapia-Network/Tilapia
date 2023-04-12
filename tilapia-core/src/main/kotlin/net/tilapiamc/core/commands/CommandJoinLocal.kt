package net.tilapiamc.core.commands

import net.tilapiamc.api.TilapiaCore
import net.tilapiamc.api.commands.BukkitCommand
import net.tilapiamc.api.commands.requiresPlayer
import net.tilapiamc.api.language.LanguageKeyDelegation
import net.tilapiamc.api.player.PlayersManager.getLocalPlayer
import net.tilapiamc.command.NetworkCommand
import net.tilapiamc.command.args.impl.stringArg
import net.tilapiamc.language.LanguageCore
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandJoinLocal: BukkitCommand("join-local", "[開發者] 加入一個伺服器的遊戲", true) {

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
                ?: commandError(localPlayer.getLanguageBundle()[LanguageCore.COULD_NOT_FIND_GAME].format(miniGameType()))

            localPlayer.sendToGame(game)

            true
        }
    }

}