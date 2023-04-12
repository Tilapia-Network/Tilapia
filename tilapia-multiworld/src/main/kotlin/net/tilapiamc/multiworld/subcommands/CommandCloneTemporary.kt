package net.tilapiamc.multiworld.subcommands

import net.tilapiamc.api.commands.BukkitSubCommand
import net.tilapiamc.api.commands.getLanguageBundle
import net.tilapiamc.api.commands.getCommandLanguageKey
import org.bukkit.ChatColor

fun commandCloneTemporary(): BukkitSubCommand.() -> Unit {

    return {
        addAlias("clone-temp")
        onCommand {
            true
        }
    }
}