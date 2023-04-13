package net.tilapiamc.multiworld.subcommands

import net.tilapiamc.api.commands.*
import net.tilapiamc.api.player.PlayersManager.getLocalPlayer
import net.tilapiamc.multiworld.args.worldNameArg
import org.bukkit.Bukkit
import org.bukkit.ChatColor

fun commandWho(): BukkitSubCommand.() -> Unit {

    return {
        val noPlayer = getCommandLanguageKey("NO_PLAYER", "${ChatColor.RED}並沒有玩家在此世界中")
        val header = getCommandLanguageKey("HEADER", "${ChatColor.GRAY}- %1\$s 的玩家清單：\n")
        val footer = getCommandLanguageKey("FOOTER", "")
        val worldName by worldNameArg("World", requireRegistered = false, requireLoaded = true, checkIllegal = false, isRequired = false)
        onCommand {
            val worldName = worldName()
            val world = if (worldName == null) requiresPlayer().world else Bukkit.getWorlds().first { it.name == worldName }
            if (world.players.isEmpty()) {
                sender.sendMessage(sender.getSenderLanguageBundle()[noPlayer])
                return@onCommand true
            }
            sender.sendMessage(getLanguageBundle()[header].format(worldName))
            for (player in world.players) {
                sender.sendMessage("${ChatColor.GRAY} - ${player.getLocalPlayer().nameWithPrefix}")
            }
            sender.sendMessage(getLanguageBundle()[footer])
            true
        }
    }
}