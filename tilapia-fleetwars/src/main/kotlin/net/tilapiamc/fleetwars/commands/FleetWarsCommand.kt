package net.tilapiamc.fleetwars.commands

import net.tilapiamc.api.TilapiaCore
import net.tilapiamc.api.commands.*
import net.tilapiamc.api.commands.args.worldNameArg
import net.tilapiamc.api.player.PlayersManager.getLocalPlayer
import net.tilapiamc.fleetwars.FleetWars
import net.tilapiamc.fleetwars.FleetWarsRules
import net.tilapiamc.sandbox.TilapiaSandbox
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.util.UUID

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
        try {
            Class.forName("net.tilapiamc.sandbox.TilapiaSandbox")
            val sandboxGames = ArrayList<UUID>()
            subCommand("sandbox", "將一局沙盒遊戲變成FleetWars沙盒地圖") {
                val success = getCommandLanguageKey("SUCCESS", "${ChatColor.GREEN}成功添加FleetWars遊戲機制至沙盒遊戲 %1\$s")
                val hint = getCommandLanguageKey("HINT", "${ChatColor.GRAY}若要停用FleetWars沙盒地圖模式，請重新開啟沙盒模式")
                val alreadyFleetWars = getCommandLanguageKey("NOT_SANDBOX", "${ChatColor.RED}此沙盒遊戲早已是FleetWars沙盒地圖")
                val notSandbox = getCommandLanguageKey("NOT_SANDBOX", "${ChatColor.RED}你並不在一局沙盒遊戲中")
                onCommand {
                    val localPlayer = requiresPlayer().getLocalPlayer()
                    val sandbox = localPlayer.currentGame
                    if (sandbox is TilapiaSandbox) {
                        if (sandbox.gameId in sandboxGames) {
                            sender.sendMessage(getLanguageBundle()[alreadyFleetWars])
                            return@onCommand true
                        }
                        sandboxGames.add(sandbox.gameId)
                        FleetWarsRules.makeFleetWarsSandbox(sandbox)
                        sender.sendMessage(getLanguageBundle()[success])
                        sender.sendMessage(getLanguageBundle()[hint])
                    } else {
                        sender.sendMessage(getLanguageBundle()[notSandbox])
                        return@onCommand true
                    }
                    true
                }
            }
        } catch (e: Throwable) {}
        onCommand {
            subCommands.first { it.name == "help" }.execute(commandAlias, sender, arrayOf("help", *rawArgs))
            true
        }
        canUseCommand {
            this is Player
        }
    }

}