package net.tilapiamc.multiworld.subcommands

import net.tilapiamc.api.commands.BukkitSubCommand
import net.tilapiamc.api.commands.getLanguageBundle
import net.tilapiamc.api.commands.getCommandLanguageKey
import net.tilapiamc.multiworld.MultiWorldCommand
import net.tilapiamc.multiworld.WorldManager
import org.bukkit.Bukkit
import org.bukkit.ChatColor

fun commandList(): BukkitSubCommand.() -> Unit {

    return {

        val header = getCommandLanguageKey("HEADER", "\n${ChatColor.GRAY}- 世界清單")
        val footer = getCommandLanguageKey("FOOTER", "")

        onCommand {
            sender.sendMessage(getLanguageBundle()[header])
            for (world in Bukkit.getWorlds()) {
                if (WorldManager.registeredWorlds.any { it.name == world.name }) continue
                if (world.name.startsWith("temp-")) {
                    sender.sendMessage(" ${ChatColor.GRAY}- ${ChatColor.GREEN}${world.name}    ${ChatColor.LIGHT_PURPLE}[${getLanguageBundle()[(parent as MultiWorldCommand).tempWorld]}]")
                } else {
                    sender.sendMessage(" ${ChatColor.GRAY}- ${ChatColor.GREEN}${world.name}    ${ChatColor.RED}[${getLanguageBundle()[(parent as MultiWorldCommand).loadedNotRegistered]}]")
                }
            }
            for (world in WorldManager.registeredWorlds) {
                if (Bukkit.getWorld(world.name) != null) {
                    sender.sendMessage(" ${ChatColor.GRAY}- ${ChatColor.GREEN}${world.name}    ${ChatColor.GREEN}[${getLanguageBundle()[(parent as MultiWorldCommand).loadedRegistered]}]")
                } else {
                    sender.sendMessage(" ${ChatColor.GRAY}- ${ChatColor.GREEN}${world.name}    ${ChatColor.YELLOW}[${getLanguageBundle()[(parent as MultiWorldCommand).registeredNotLoaded]}]")
                }
            }
            sender.sendMessage(getLanguageBundle()[footer])
            true
        }
    }
}