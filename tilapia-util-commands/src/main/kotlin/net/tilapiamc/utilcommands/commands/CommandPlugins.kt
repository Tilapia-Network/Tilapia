package net.tilapiamc.utilcommands.commands

import net.md_5.bungee.api.ChatColor.*
import net.tilapiamc.api.TilapiaCore
import net.tilapiamc.api.commands.BukkitCommand
import net.tilapiamc.api.commands.requiresPlayer
import net.tilapiamc.common.language.LanguageKeyDelegation
import net.tilapiamc.api.player.PlayersManager.getLocalPlayer
import org.bukkit.Bukkit

class CommandPlugins: BukkitCommand("plugins", "列出所有的插件", false) {

    companion object {
        val COMMAND_PLUGIN_HEADER by LanguageKeyDelegation(
            "$YELLOW - 插件清單\n" +
            "$GRAY 我們刻意保持此指令公開，你可以看到我們所有使用的插件。\n" +
            "$GRAY (所有插件開頭為\"${GREEN}tilapia$GRAY\"皆為自製插件）"
        )
        val COMMAND_PLUGIN_FOOTER by LanguageKeyDelegation("")

        init {
            TilapiaCore.instance.languageManager.registerLanguageKey(COMMAND_PLUGIN_HEADER)
            TilapiaCore.instance.languageManager.registerLanguageKey(COMMAND_PLUGIN_FOOTER)
        }
    }

    init {
        addAlias("pl")

        onCommand {
            val player = requiresPlayer().getLocalPlayer()
            player.sendMessage(player.getLanguageBundle()[COMMAND_PLUGIN_HEADER])
            for (plugin in Bukkit.getPluginManager().plugins) {
                player.sendMessage("$GRAY - $GREEN${plugin.name}")
            }
            player.sendMessage(player.getLanguageBundle()[COMMAND_PLUGIN_FOOTER])
            true
        }
    }

}