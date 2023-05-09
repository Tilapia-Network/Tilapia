package net.tilapiamc.sandbox.commands

import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.tilapiamc.api.TilapiaCore
import net.tilapiamc.api.commands.*
import net.tilapiamc.api.game.ManagedGame
import net.tilapiamc.api.player.PlayersManager.getLocalPlayer
import net.tilapiamc.sandbox.SandboxProperties
import net.tilapiamc.sandbox.TilapiaSandbox
import net.tilapiamc.sandbox.commands.args.*
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player

class CommandSandbox: BukkitCommand("sandbox", "沙盒模式主要指令", true) {

    val core = TilapiaCore.instance

    init {
        addAlias("sb")
        val clickToTeleport = getCommandLanguageKey("CLICK_TO_TELEPORT", "${ChatColor.YELLOW}[點我傳送]")
        val hoverText = getCommandLanguageKey("CLICK_TO_TELEPORT_HOVER_TEXT", "${ChatColor.GRAY}執行 %1\$s")

        subCommand("help", "查看沙盒模式的指令列表") {
            val header = getCommandLanguageKey("HEADER", "\n${ChatColor.GREEN}========== 沙盒模式 (/sandbox) ==========")
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
            val errorSandboxAlreadyExists = getCommandLanguageKey("ERROR_SANDBOX_ALREADY_EXISTS", "${ChatColor.RED}該沙盒地圖早已存在！")
            val worldName by worldNameArg("World", true)
            onCommand {
                val worldName = worldName()!!
                if (core.gameFinder.findLobbies("sandbox").any {
                        it.getProperty(SandboxProperties.SANDBOX_WORLD)?.asString == worldName
                    }) {
                    sender.sendMessage("")

                    val text = getLanguageBundle()[errorSandboxAlreadyExists]
                    val command = "/sandbox tp $worldName"
                    if (sender is Player) {
                        requiresPlayer().spigot().sendMessage(TextComponent("$text    ").also {
                            it.addExtra(TextComponent(getLanguageBundle()[clickToTeleport]).also { button ->
                                button.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, command)
                                button.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, arrayOf(TextComponent(getLanguageBundle()[hoverText].format(command))))
                            })
                        })
                    } else {
                        sender.sendMessage(text)
                    }
                    sender.sendMessage("")
                    return@onCommand true
                }
                if (core.localGameManager.getAllLocalGames().filterIsInstance<ManagedGame>().any { it.gameWorld.name == worldName }) {
                    sender.sendMessage("")
                    sender.sendMessage(getLanguageBundle()[errorWorldAlreadyAssigned])
                    sender.sendMessage("")
                    return@onCommand true
                }
                core.addGame(TilapiaSandbox(core, Bukkit.getWorld(worldName)))
                if (sender !is Player) {
                    sender.sendMessage(getLanguageBundle()[success])
                } else {
                    val command = "/sandbox tp $worldName"
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
        subCommand("stop-local", "停止沙盒模式") {
            val success = getCommandLanguageKey("SUCCESS", "${ChatColor.GREEN}成功結束沙盒地圖")
            val sandBox by localSandBoxArg("SandBox", true)
            onCommand {
                val sandBox = sandBox()!!
                sender.sendMessage(getLanguageBundle()[success])
                sandBox.end()
                true
            }
        }

        subCommand("list", "列出正在運行的沙盒地圖") {
            val noSandboxRunning = getCommandLanguageKey("NO_SANDBOX_RUNNING", "${ChatColor.RED}目前並沒有任何沙盒地圖")
            val header = getCommandLanguageKey("HEADER", "${ChatColor.GRAY}沙盒地圖列表: ")
            val footer = getCommandLanguageKey("FOOTER", "")
            onCommand {
                val sandboxes = core.gameFinder.findLobbies("sandbox").filter {
                    it.hasProperty(SandboxProperties.SANDBOX_WORLD)
                }
                if (sandboxes.isEmpty()) {
                    sender.sendMessage("")
                    sender.sendMessage(getLanguageBundle()[noSandboxRunning])
                    sender.sendMessage("")
                    return@onCommand true
                }
                sender.sendMessage(getLanguageBundle()[header])
                sender.sendMessage("")
                for (sandbox in sandboxes) {
                    val worldName = sandbox.getProperty(SandboxProperties.SANDBOX_WORLD)!!.asString
                    val text = "${ChatColor.GRAY}- ${ChatColor.GREEN}${worldName}"
                    val command = "/sandbox tp $worldName"
                    if (sender is Player) {
                        requiresPlayer().spigot().sendMessage(TextComponent("$text    ").also {
                            it.addExtra(TextComponent(getLanguageBundle()[clickToTeleport]).also { button ->
                                button.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, command)
                                button.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, arrayOf(TextComponent(getLanguageBundle()[hoverText].format(command))))
                            })
                        })
                    } else {
                        sender.sendMessage(text)
                    }
                }
                sender.sendMessage(getLanguageBundle()[footer])
                true
            }
        }
        subCommand("tp", "傳送到一張沙盒地圖") {
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