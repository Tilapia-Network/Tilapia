package net.tilapiamc.multiworld.subcommands

import net.tilapiamc.api.commands.BukkitSubCommand
import net.tilapiamc.api.commands.getLanguageBundle
import net.tilapiamc.api.commands.getCommandLanguageKey
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
import java.io.File

fun commandImport(): BukkitSubCommand.() -> Unit {

    return {
        val success = getCommandLanguageKey("SUCCESS", "${ChatColor.GREEN}成功註冊 ${ChatColor.YELLOW}%1\$s ${ChatColor.GREEN}！")
        val worldDoesNotExist = getCommandLanguageKey("WORLD_DOES_NOT_EXIST", "${ChatColor.RED}名為 ${ChatColor.YELLOW}%1\$s ${ChatColor.RED} 的世界並不存在伺服器資料夾。")
        val worldName by worldNameArg("WorldName", false, false, true)
        val worldType by enumArg<CommandSender, WorldType>("WorldType", { false })
        val generator by generatorArg("Generator", isRequired = false)
        onCommand {
            if (WorldManager.registeredWorlds.any { it.name.lowercase() == worldName()!!.lowercase() }) {
                throw WorldAlreadyExists(worldName()!!)
            }
            if (!Bukkit.getWorlds().any { it.name.lowercase() == worldName()!!.lowercase() } && (!File(worldName()!!).exists() || !File(worldName()!!).isDirectory)) {
                sender.sendMessage(getLanguageBundle()[worldDoesNotExist].format(worldName()))
                return@onCommand true
            }
            val chunkGen = generator()
            val tilapiaWorld = TilapiaWorld(worldName()!!, worldType()!!, chunkGen?.name, chunkGen?.parameter)
            try {
                if (!Bukkit.getWorlds().any { it.name.lowercase() == worldName()!!.lowercase() }) {
                    WorldManager.createWorld(tilapiaWorld)
                }
                WorldManager.registerWorld(tilapiaWorld)
            } catch (e: IllegalArgumentException) {
                throw CommandException(e.message)
            }
            sender.sendMessage(getLanguageBundle()[success].format(worldName()))
            true
        }
    }
}