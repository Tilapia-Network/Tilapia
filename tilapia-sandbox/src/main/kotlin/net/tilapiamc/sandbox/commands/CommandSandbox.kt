package net.tilapiamc.sandbox.commands

import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.tilapiamc.api.TilapiaCore
import net.tilapiamc.api.commands.*
import net.tilapiamc.api.game.ManagedGame
import net.tilapiamc.api.player.PlayersManager.getLocalPlayer
import net.tilapiamc.sandbox.TilapiaSandbox
import net.tilapiamc.sandbox.commands.args.SandBoxNotFoundException
import net.tilapiamc.sandbox.commands.args.WorldNotFoundException
import net.tilapiamc.sandbox.commands.args.sandBoxArg
import net.tilapiamc.sandbox.commands.args.worldNameArg
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player

class CommandSandbox: BukkitCommand("sandbox", "沙盒模式主要指令", true) {

    val core = TilapiaCore.instance

    init {

        val clickToTeleport = getCommandLanguageKey("CLICK_TO_TELEPORT", "${ChatColor.YELLOW}[點我傳送]")
        val hoverText = getCommandLanguageKey("CLICK_TO_TELEPORT_HOVER_TEXT", "${ChatColor.GRAY}執行 %1\$s")

        subCommand("help", "查看沙盒模式的指令列表") {
            val header = getCommandLanguageKey("HEADER", "\n${ChatColor.GREEN}========== 沙河模式 (/sandbox) ==========")
            onCommand {
                sender.sendMessage(getLanguageBundle()[header])

                for (subCommand in parent.subCommands) {
                    sender.sendMessage("${ChatColor.YELLOW} /sandbox ${subCommand.name} ${subCommand.getUsageString()}  -  ${ChatColor.AQUA}${(subCommand as BukkitSubCommand).getDescription(getLanguageBundle())}")
                }
                sender.sendMessage("")
                true
            }
        }
        subCommand("start", "開始沙盒模式") {
            val success = getCommandLanguageKey("SUCCESS", "${ChatColor.GREEN}成功開始沙盒地圖!  ")
            val errorWorldAlreadyAssigned = getCommandLanguageKey("ERROR_WORLD_ALREADY_ASSIGNED", "${ChatColor.GREEN}該世界以被其他遊戲使用")
            val worldName by worldNameArg("World", true)
            onCommand {
                val worldName = worldName()!!
                if (TilapiaCore.instance.localGameManager.getAllLocalGames().filterIsInstance<ManagedGame>().any { it.gameWorld.name == worldName }) {
                    sender.sendMessage("")
                    sender.sendMessage(getLanguageBundle()[errorWorldAlreadyAssigned])
                    sender.sendMessage("")
                    return@onCommand true
                }
                TilapiaCore.instance.addGame(TilapiaSandbox(TilapiaCore.instance, Bukkit.getWorld(worldName)))
                if (sender !is Player) {
                    sender.sendMessage(getLanguageBundle()[success])
                } else {
                    val command = "/sandbox tp-local $worldName"
                    requiresPlayer().spigot().sendMessage(TextComponent(getLanguageBundle()[success]).also {
                        it.addExtra(TextComponent(getLanguageBundle()[clickToTeleport]).also { button ->
                            button.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, command)
                            button.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, arrayOf(TextComponent(getLanguageBundle()[hoverText].format(command))))
                        })
                    })
                }
                true
            }
        }
        subCommand("stop", "停止沙盒模式") {
            val success = getCommandLanguageKey("SUCCESS", "${ChatColor.GREEN}成功結束沙盒地圖")
            val sandBox by sandBoxArg("SandBox", true)
            onCommand {
                val sandBox = sandBox()!!
                sandBox.end()
                sender.sendMessage(getLanguageBundle()[success])
                true
            }
        }
        subCommand("list", "列出所有正在運行的沙盒地圖") {
            onCommand {
                sender.sendMessage("TODO: Not yet implemented")
                true
            }
        }
        subCommand("list-local", "列出此伺服器正在運行的沙盒地圖") {
            val header = getCommandLanguageKey("HEADER", "${ChatColor.GRAY}- 沙盒地圖列表")
            val footer = getCommandLanguageKey("FOOTER", "")
            onCommand {
                sender.sendMessage(getLanguageBundle()[header])
                for (tilapiaSandbox in TilapiaCore.instance.localGameManager.getAllLocalGames()
                    .filterIsInstance<TilapiaSandbox>()) {
                    val text = "${ChatColor.GRAY}- ${ChatColor.GREEN}${tilapiaSandbox.gameWorld.name}"
                    val command = "/sandbox tp-local ${tilapiaSandbox.gameWorld.name}"
                    if (sender is Player) {
                        requiresPlayer().spigot().sendMessage(TextComponent(text).also {
                            TextComponent(getLanguageBundle()[clickToTeleport]).also { button ->
                                button.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, command)
                                button.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, arrayOf(TextComponent(getLanguageBundle()[hoverText].format(command))))
                            }
                        })
                    } else {
                        sender.sendMessage(text)
                    }
                }
                sender.sendMessage(getLanguageBundle()[footer])
                true
            }
        }
        subCommand("tp-local", "傳送到此伺服器的一張沙盒地圖") {
            val sandBox by sandBoxArg("SandBox", true)
            onCommand {
                val sandBox = sandBox()
                requiresPlayer().getLocalPlayer().sendToGame(sandBox!!, true)
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

        val sandBoxNotFound = getCommandLanguageKey("ERROR_SANDBOX_NOT_FOUND", "${ChatColor.RED}找不到名為 %1\$s 的沙盒世界！")
        val worldNotLoaded = getCommandLanguageKey("ERROR_WORLD_NOT_LOADED", "${ChatColor.RED}世界 %1\$s 並未被載入！")
        exceptionHandlers.add { e, sender, args ->
            if (e is WorldNotFoundException) {
                sender.sendMessage(sender.getSenderLanguageBundle()[worldNotLoaded].format(e.worldName))
                true
            } else if (e is SandBoxNotFoundException) {
                sender.sendMessage(sender.getSenderLanguageBundle()[sandBoxNotFound].format(e.worldName))
                true
            } else {
                false
            }
        }
    }

}