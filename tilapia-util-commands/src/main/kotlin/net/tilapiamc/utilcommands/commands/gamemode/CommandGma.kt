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

class CommandGma: BukkitCommand("gma", "將你的遊戲模式調整為冒險", true) {



    init {
        addAlias("gm2")

        onCommand {
            val player = requiresPlayer().getLocalPlayer()
            player.gameMode = GameMode.ADVENTURE
            player.sendMessage(player.getLanguageBundle()[LanguageCore.COMMAND_GAMEMODE_SUCCESS].format(player.gameMode.name))
            true
        }

        canUseCommand {
            this is Player
        }
    }

}