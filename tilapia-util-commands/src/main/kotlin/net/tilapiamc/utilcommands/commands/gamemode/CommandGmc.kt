package net.tilapiamc.utilcommands.commands.gamemode

import net.tilapiamc.api.commands.BukkitCommand
import net.tilapiamc.api.commands.requiresPlayer
import net.tilapiamc.api.player.PlayersManager.getLocalPlayer
import net.tilapiamc.language.LanguageCore
import org.bukkit.GameMode
import org.bukkit.entity.Player

class CommandGmc: BukkitCommand("gmc", "將你的遊戲模式調整為創造", true) {



    init {
        addAlias("gm1")

        onCommand {
            val player = requiresPlayer().getLocalPlayer()
            player.gameMode = GameMode.CREATIVE
            player.sendMessage(player.getLanguageBundle()[LanguageCore.COMMAND_GAMEMODE_SUCCESS].format(player.gameMode.name))
            true
        }

        canUseCommand {
            this is Player
        }
    }
}