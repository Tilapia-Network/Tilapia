package net.tilapiamc.multiworld.subcommands

import net.tilapiamc.api.commands.BukkitSubCommand
import net.tilapiamc.api.commands.getLanguageBundle
import net.tilapiamc.api.commands.getCommandLanguageKey
import org.bukkit.ChatColor

fun commandTp(): BukkitSubCommand.() -> Unit {

    return {
        addAlias("teleport")
        onCommand {
            true
        }
    }
}