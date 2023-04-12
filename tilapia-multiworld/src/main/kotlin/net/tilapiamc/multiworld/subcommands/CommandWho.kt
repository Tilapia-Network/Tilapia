package net.tilapiamc.multiworld.subcommands

import net.tilapiamc.api.commands.*
import net.tilapiamc.api.player.PlayersManager.getLocalPlayer
import net.tilapiamc.multiworld.args.worldNameArg
import org.bukkit.Bukkit
import org.bukkit.ChatColor

fun commandWho(): BukkitSubCommand.() -> Unit {

    return {
        val noPlayer = getCommandLanguageKey("NO_PLAYER", "${ChatColor.RED}並沒有玩家在此世界中")

        val worldName by worldNameArg("World", requireRegistered = false, requireLoaded = true, isRequired = false)
        onCommand {
            val world = if (worldName() == null) requiresPlayer().world else Bukkit.getWorlds().first { it.name == worldName() }
            if (world.players.isEmpty()) {
                sender.sendMessage(sender.getSenderLanguageBundle()[noPlayer])
                return@onCommand true
            }
            sender.sendMessage("")
            for (player in world.players) {
                sender.sendMessage("${ChatColor.GRAY} - ${player.getLocalPlayer().nameWithPrefix}")
            }
            sender.sendMessage("")
            true
        }
    }
}