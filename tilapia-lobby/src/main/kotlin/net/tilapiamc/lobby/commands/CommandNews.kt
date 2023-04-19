package net.tilapiamc.lobby.commands

import net.tilapiamc.api.commands.BukkitCommand
import net.tilapiamc.api.commands.requiresPlayer
import net.tilapiamc.api.player.PlayersManager.getLocalPlayer
import net.tilapiamc.lobby.plugins.PluginNews
import org.bukkit.entity.Player

class CommandNews(val plugin: PluginNews): BukkitCommand("news", "顯示最新資料") {

    init {
        onCommand {
            plugin.show(requiresPlayer())
            true
        }

        canUseCommand {
            this is Player && this.getLocalPlayer().currentGame == plugin.game
        }
    }

}