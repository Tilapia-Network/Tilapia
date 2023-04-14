package net.tilapiamc.multiworld.subcommands

import net.tilapiamc.api.commands.BukkitSubCommand
import net.tilapiamc.api.commands.getCommandLanguageKey
import net.tilapiamc.api.commands.getLanguageBundle
import net.tilapiamc.command.args.impl.enumArg
import net.tilapiamc.multiworld.TilapiaWorld
import net.tilapiamc.multiworld.WorldAlreadyExists
import net.tilapiamc.multiworld.WorldManager
import net.tilapiamc.multiworld.args.generatorArg
import net.tilapiamc.multiworld.args.seedArg
import net.tilapiamc.multiworld.args.worldNameArg
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.WorldType
import org.bukkit.command.CommandException
import org.bukkit.command.CommandSender

fun commandCreate(): BukkitSubCommand.() -> Unit {

    return {
        val success = getCommandLanguageKey("SUCCESS", "${ChatColor.GREEN}成功創建${ChatColor.YELLOW} %1\$s ${ChatColor.GREEN}！")
        val worldAlreadyLoaded = getCommandLanguageKey("WORLD_ALREADY_LOADED", "${ChatColor.RED}名為 ${ChatColor.YELLOW}%1\$s ${ChatColor.RED} 的世界早已被載入！請使用 \"/mv import\"註冊世界")
        val worldName by worldNameArg("WorldName", false, false, true)
        val worldType by enumArg<CommandSender, WorldType>("WorldType", { false })
        val seed by seedArg("Seed", isRequired = false)
        val generator by generatorArg("Generator", isRequired = false)
        onCommand {
            if (WorldManager.registeredWorlds.any { it.name.lowercase() == worldName()!!.lowercase() }) {
                throw WorldAlreadyExists(worldName()!!)
            }
            if (Bukkit.getWorlds().any { it.name.lowercase() == worldName()!!.lowercase() }) {
                sender.sendMessage(getLanguageBundle()[worldAlreadyLoaded].format(worldName()))
                return@onCommand true
            }
            val chunkGen = generator()
            val tilapiaWorld = TilapiaWorld(worldName()!!, worldType()!!, chunkGen?.name, chunkGen?.parameter)
            try {
                WorldManager.createWorld(tilapiaWorld, seed())
                WorldManager.registerWorld(tilapiaWorld)
            } catch (e: IllegalArgumentException) {
                throw CommandException(e.message)
            }
            sender.sendMessage(getLanguageBundle()[success].format(worldName()))
            true
        }
    }
}