package net.tilapiamc.fleetwars.commands

import net.tilapiamc.api.TilapiaCore
import net.tilapiamc.api.commands.*
import net.tilapiamc.api.commands.args.worldNameArg
import net.tilapiamc.api.player.PlayersManager.getLocalPlayer
import net.tilapiamc.fleetwars.FleetWars
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player

class FleetWarsCommand: BukkitCommand("fleetwars", "FleetWars 的主要指令", true) {

    init {
        subCommand("start", "手動開始一局FleetWars遊戲") {
            val starting = getCommandLanguageKey("STARTING", "${ChatColor.GREEN}正在開始FleetWars...")
            val worldName by worldNameArg("World", true)
            onCommand {
                worldName()
                sender.sendMessage("")
                sender.sendMessage(getLanguageBundle()[starting])
                sender.sendMessage("")
                val game = FleetWars(TilapiaCore.instance, Bukkit.getWorld(worldName()))
                TilapiaCore.instance.addGame(game)
                requiresPlayer().getLocalPlayer().sendToGame(game)
                true
            }
        }
        subCommand("help", "查看Fleetwars的指令列表") {
            val header = getCommandLanguageKey("HEADER", "\n${ChatColor.GREEN}========== FleetWars (/fleetwars) ==========")
            onCommand {
                sender.sendMessage(getLanguageBundle()[header])

                for (subCommand in parent.subCommands) {
                    sender.sendMessage("${ChatColor.YELLOW} /fleetwars ${subCommand.name} ${subCommand.getUsageString()}  -  ${ChatColor.AQUA}${(subCommand as BukkitSubCommand).getDescription(getLanguageBundle())}")
                }
                sender.sendMessage("")
                true
            }
        }
        onCommand {
            subCommands.first { it.name == "help" }.execute(commandAlias, sender, arrayOf("help", *rawArgs))
            true
        }
        canUseCommand {
            this is Player
        }
    }

}