package net.tilapiamc.multiworld.subcommands

import net.tilapiamc.api.commands.BukkitSubCommand
import net.tilapiamc.api.commands.getLanguageBundle
import net.tilapiamc.api.commands.getCommandLanguageKey
import org.bukkit.ChatColor

fun commandHelp(): BukkitSubCommand.() -> Unit {

    return {
        val header = getCommandLanguageKey("HEADER", "\n${ChatColor.GREEN}========== 多世界插件 (/mv) ==========")
        onCommand {

            sender.sendMessage(getLanguageBundle()[header])

            for (subCommand in parent.subCommands) {
                sender.sendMessage("${ChatColor.YELLOW} /mw ${subCommand.name} ${subCommand.getUsageString()}  -  ${ChatColor.AQUA}${(subCommand as BukkitSubCommand).getDescription(getLanguageBundle())}")
            }
            sender.sendMessage("")
            true
        }
    }
}