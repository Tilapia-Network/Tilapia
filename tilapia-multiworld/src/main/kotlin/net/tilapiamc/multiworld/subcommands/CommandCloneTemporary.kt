package net.tilapiamc.multiworld.subcommands

import net.tilapiamc.api.commands.BukkitSubCommand

fun commandCloneTemporary(): BukkitSubCommand.() -> Unit {

    return {
        addAlias("clone-temp")
        onCommand {
            true
        }
    }
}