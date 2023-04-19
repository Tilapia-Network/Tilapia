package net.tilapiamc.multiworld.subcommands

import net.tilapiamc.api.commands.BukkitSubCommand
import net.tilapiamc.api.commands.getCommandLanguageKey
import net.tilapiamc.api.commands.getLanguageBundle
import net.tilapiamc.api.commands.requiresPlayer
import net.tilapiamc.multiworld.WorldManager
import net.tilapiamc.multiworld.args.worldNameArg
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandException
import java.io.File
import java.util.*

fun commandDelete(): BukkitSubCommand.() -> Unit {

    val confirm = HashMap<UUID, Long>()

    return {
        val success = getCommandLanguageKey("SUCCESS", "${ChatColor.GREEN}成功刪除 ${ChatColor.YELLOW}%1\$s ${ChatColor.GREEN}！")
        val confirmNeeded = getCommandLanguageKey("CONFIRM_NEEDED", "${ChatColor.RED}這將會永久刪除世界，請重新輸入一次指令確認")
        val worldName by worldNameArg("WorldName", true, false, true)
        onCommand {
            val worldName = worldName()!!
            if (System.currentTimeMillis() > (confirm[requiresPlayer().uniqueId] ?: 0)) {
                sender.sendMessage(getLanguageBundle()[confirmNeeded])
                confirm[requiresPlayer().uniqueId] = System.currentTimeMillis() + 3000
                return@onCommand true
            }
            Bukkit.unloadWorld(worldName, false)
            try {
                WorldManager.unregisterWorld(worldName)
            } catch (e: IllegalArgumentException) {
                throw CommandException(e.message)
            }
            File(worldName).deleteRecursively()
            sender.sendMessage(getLanguageBundle()[success].format(worldName))
            confirm.remove(requiresPlayer().uniqueId)
            true
        }
    }
}