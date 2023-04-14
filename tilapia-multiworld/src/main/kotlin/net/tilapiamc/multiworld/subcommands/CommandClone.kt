package net.tilapiamc.multiworld.subcommands

import net.tilapiamc.api.commands.BukkitSubCommand

fun commandClone(): BukkitSubCommand.() -> Unit {

    return {
        onCommand {

            true
        }
    }
}