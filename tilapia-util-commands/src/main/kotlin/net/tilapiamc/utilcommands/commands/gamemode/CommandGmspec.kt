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

class CommandGmspec: BukkitCommand("gmspec", "將你的遊戲模式調整為旁觀者") {



    init {
        addAlias("gm3")

        onCommand {
            val player = requiresPlayer().getLocalPlayer()
            player.gameMode = GameMode.SPECTATOR
            player.sendMessage(player.getLanguageBundle()[LanguageCore.COMMAND_GAMEMODE_SUCCESS].format(player.gameMode.name))
            true
        }

        canUseCommand {
            isOp && this is Player
        }
    }

}