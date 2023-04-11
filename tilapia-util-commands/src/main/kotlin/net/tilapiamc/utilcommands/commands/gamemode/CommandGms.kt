package net.tilapiamc.utilcommands.commands.gamemode

import net.md_5.bungee.api.ChatColor.*
import net.tilapiamc.api.commands.BukkitCommand
import net.tilapiamc.api.commands.requiresPlayer
import net.tilapiamc.api.language.LanguageKeyDelegation
import net.tilapiamc.api.player.PlayersManager.getLocalPlayer
import net.tilapiamc.language.LanguageCore
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.entity.Player

class CommandGms: BukkitCommand("gms", "將你的遊戲模式調整為生存") {



    init {
        addAlias("gm0")

        onCommand {
            val player = requiresPlayer().getLocalPlayer()
            player.gameMode = GameMode.SURVIVAL
            player.sendMessage(player.getLanguageBundle()[LanguageCore.COMMAND_GAMEMODE_SUCCESS].format(player.gameMode.name))
            true
        }

        canUseCommand {
            isOp && this is Player
        }
    }

}