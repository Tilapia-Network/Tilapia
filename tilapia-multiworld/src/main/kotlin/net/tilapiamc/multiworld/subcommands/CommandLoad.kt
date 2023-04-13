package net.tilapiamc.multiworld.subcommands

import net.tilapiamc.api.TilapiaCore
import net.tilapiamc.api.commands.BukkitSubCommand
import net.tilapiamc.api.commands.getLanguageBundle
import net.tilapiamc.api.commands.getCommandLanguageKey
import net.tilapiamc.command.args.impl.enumArg
import net.tilapiamc.command.args.impl.stringArg
import net.tilapiamc.command.args.impl.stringEnumArg
import net.tilapiamc.multiworld.TilapiaWorld
import net.tilapiamc.multiworld.WorldAlreadyExists
import net.tilapiamc.multiworld.WorldManager
import net.tilapiamc.multiworld.WorldNotFoundException
import net.tilapiamc.multiworld.args.generatorArg
import net.tilapiamc.multiworld.args.worldNameArg
import net.tilapiamc.spigotcommon.game.LocalGame
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.WorldType
import org.bukkit.command.CommandException
import org.bukkit.command.CommandSender
import java.io.File

fun commandLoad(): BukkitSubCommand.() -> Unit {

    return {
        val success = getCommandLanguageKey("SUCCESS", "${ChatColor.GREEN}成功加載 ${ChatColor.YELLOW}%1\$s ${ChatColor.GREEN}！")
        val worldName by worldNameArg("WorldName", true, false, false)
        onCommand {
            try {
                WorldManager.createWorld(WorldManager.registeredWorlds.first { it.name == worldName() })
            } catch (e: IllegalArgumentException) {
                throw CommandException(e.message)
            }
            sender.sendMessage(getLanguageBundle()[success].format(worldName()))
            true
        }
    }
}