package net.tilapiamc.utilcommands.commands

import net.md_5.bungee.api.ChatColor.*
import net.tilapiamc.api.commands.BukkitCommand
import net.tilapiamc.api.commands.args.playerArg
import net.tilapiamc.api.commands.requiresPlayer
import net.tilapiamc.api.language.LanguageKeyDelegation
import net.tilapiamc.api.player.PlayersManager.getLocalPlayer
import net.tilapiamc.command.args.impl.stringEnumArg
import net.tilapiamc.language.LanguageCore
import org.bukkit.ChatColor
import org.bukkit.entity.Player

class CommandForceFly: BukkitCommand("forcefly", "強制更改一個玩家的飛行模式") {

    companion object {
        val COMMAND_FORCEFLY_ENABLE by LanguageKeyDelegation(
            "${ChatColor.GREEN}成功啟用 ${ChatColor.YELLOW}%1\$s${ChatColor.GREEN} 的飛行模式"
        )
        val COMMAND_FORCEFLY_DISABLE by LanguageKeyDelegation(
            "${ChatColor.RED}成功停用 ${ChatColor.YELLOW}%1\$s${ChatColor.RED} 的飛行模式"
        )
    }

    init {
        addAlias("gm")

        val mode by stringEnumArg("Mode", { arrayListOf("enable", "disable") }, isRequired = false)
        val player by playerArg("Player", false)

        onCommand {
            val targetPlayer = player()?.getLocalPlayer()?:requiresPlayer().getLocalPlayer()
            val enabled = if (mode() == null) !targetPlayer.allowFlight else mode() == "enable"
            targetPlayer.allowFlight = enabled
            if (enabled) {
                targetPlayer.sendMessage(targetPlayer.getLanguageBundle()[COMMAND_FORCEFLY_ENABLE].format(targetPlayer.name))
            } else {
                targetPlayer.sendMessage(targetPlayer.getLanguageBundle()[COMMAND_FORCEFLY_DISABLE].format(targetPlayer.name))
            }
            true
        }

        canUseCommand {
            isOp && this is Player
        }
    }

}