package net.tilapiamc.multiworld.subcommands

import net.tilapiamc.api.TilapiaCore
import net.tilapiamc.api.commands.BukkitSubCommand
import net.tilapiamc.api.commands.getLanguageBundle
import net.tilapiamc.api.commands.getCommandLanguageKey
import net.tilapiamc.multiworld.WorldManager
import net.tilapiamc.multiworld.args.worldNameArg
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandException

fun commandRemove(): BukkitSubCommand.() -> Unit {

    return {
        val success = getCommandLanguageKey("SUCCESS", "${ChatColor.GREEN}成功取消註冊 ${ChatColor.YELLOW}%1\$s ${ChatColor.GREEN}！")
        val worldName by worldNameArg("WorldName", true, false, true)
        onCommand {
            val worldName = worldName()
            try {
                WorldManager.unregisterWorld(worldName!!)
            } catch (e: IllegalArgumentException) {
                throw CommandException(e.message)
            }
            sender.sendMessage(getLanguageBundle()[success].format(worldName))
            true
        }
    }
}