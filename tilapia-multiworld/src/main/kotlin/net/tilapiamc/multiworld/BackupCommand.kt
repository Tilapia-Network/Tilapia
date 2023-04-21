package net.tilapiamc.multiworld

import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.tilapiamc.api.commands.*
import net.tilapiamc.command.args.impl.stringArg
import net.tilapiamc.database.blockingDbQuery
import net.tilapiamc.multiworld.args.worldNameArg
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.selectAll
import java.io.File
import java.util.*

class BackupCommand(val saveManager: WorldSaveManager): BukkitCommand("backup", "創建世界的存檔", true) {

    val saveOverrideTimeout = HashMap<UUID, Long>()

    init {
        val hoverText = getCommandLanguageKey("CLICK_TO_EXECUTE", "${ChatColor.GRAY}執行 %1\$s")

        subCommand("save", "儲存一個世界") {
            val worldName by worldNameArg("WorldName", requireRegistered = true, requireLoaded = false, checkIllegal = true, isRequired = true)
            val saveName by stringArg("SaveName", false)

            val success = getCommandLanguageKey("SUCCESS", "\n${ChatColor.GREEN}成功除存世界 %1\$s")
            val worldNotFound = getCommandLanguageKey("WORLD_ALREADY_EXISTS", "\n${ChatColor.RED}同樣名稱的世界早已存在！ 請再輸入一次指令確認除存")
            onCommand {
                Thread {
                    val finalWorldName = worldName()!!
                    val finalSaveName = "${saveName() ?: finalWorldName.split("__BACKUP__")[0]}__BACKUP__${System.currentTimeMillis()}"
                    Bukkit.getWorld(finalWorldName)?.save()
                    if (System.currentTimeMillis() > (saveOverrideTimeout[requiresPlayer().uniqueId]?:0) && saveManager.has(finalSaveName)) {
                        saveOverrideTimeout[requiresPlayer().uniqueId] = System.currentTimeMillis() + 3000
                        sender.sendMessage("")
                        sender.sendMessage(getLanguageBundle()[worldNotFound])
                        sender.sendMessage("")
                        return@Thread
                    }
                    saveOverrideTimeout.remove(requiresPlayer().uniqueId)
                    saveManager.save(finalSaveName, WorldManager.getWorld(finalWorldName)!!, false)

                    sender.sendMessage("")
                    sender.sendMessage(getLanguageBundle()[success].format(finalSaveName))
                    sender.sendMessage("")
                }.start()

                true
            }
        }
        subCommand("load", "載入一個世界") {
            val saveName by stringArg("SaveName", true)
            val worldName by worldNameArg("WorldName", requireRegistered = false, requireLoaded = false, checkIllegal = true, isRequired = false)

            val success = getCommandLanguageKey("SUCCESS", "\n${ChatColor.GREEN}成功載入世界 %1\$s")
            val worldNotFound = getCommandLanguageKey("WORLD_NOT_FOUND", "\n${ChatColor.RED}找不到該存檔 %1\$s")
            val worldAlreadyExists = getCommandLanguageKey("WORLD_ALREADY_EXISTS", "\n${ChatColor.RED}世界早已存在！若要覆蓋，請刪除世界; 若要使用其他世界名稱，請使用 /backup load <存檔名稱> <世界名稱>")
            val clickToCreateSandBoxWorld = getCommandLanguageKey("CLICK_TO_CREATE_SANDBOX", "\n${ChatColor.YELLOW}[點我創建沙盒世界]")
            onCommand {
                val finalSaveName = saveName()!!
                val finalWorldName = worldName()?:finalSaveName.split("__BACKUP__")[0]
                val world = saveManager.load(finalSaveName, finalWorldName)
                if (world == null) {
                    sender.sendMessage("")
                    sender.sendMessage(getLanguageBundle()[worldNotFound].format(finalSaveName))
                    sender.sendMessage("")
                    return@onCommand true
                }
                if (File(finalWorldName).exists()) {
                    sender.sendMessage("")
                    sender.sendMessage(getLanguageBundle()[worldAlreadyExists])
                    sender.sendMessage("")
                    return@onCommand true
                }
                sender.sendMessage("")
                sender.sendMessage(getLanguageBundle()[success].format(finalSaveName))
                val command = "/sandbox start $finalWorldName"
                requiresPlayer().spigot().sendMessage(TextComponent(getLanguageBundle()[clickToCreateSandBoxWorld].format(finalWorldName)).also { button ->
                    button.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, command)
                    button.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, arrayOf(TextComponent(getLanguageBundle()[hoverText].format(command))))
                })
                sender.sendMessage("")
                true
            }
        }
        val deleteConfirm = HashMap<UUID, Long>()
        subCommand("delete", "刪除一個存檔") {
            val saveName by stringArg("SaveName", true)
            val success = getCommandLanguageKey("SUCCESS", "\n${ChatColor.GREEN}成功刪除世界 %1\$s")
            val confirmNeeded = getCommandLanguageKey("CONFIRM_NEEDED", "\n${ChatColor.RED}此動作將會永久刪除世界！請重新輸入指令以確認")
            val worldNotFound = getCommandLanguageKey("WORLD_NOT_FOUND", "\n${ChatColor.RED}找不到該存檔 %1\$s")
            onCommand {
                val finalSaveName = saveName()!!
                if (!saveManager.has(finalSaveName)) {
                    sender.sendMessage("")
                    sender.sendMessage(getLanguageBundle()[worldNotFound].format(finalSaveName))
                    sender.sendMessage("")
                    return@onCommand true
                }
                if (System.currentTimeMillis() > (deleteConfirm[requiresPlayer().uniqueId]?:0)) {
                    deleteConfirm[requiresPlayer().uniqueId] = System.currentTimeMillis() + 3000
                    sender.sendMessage("")
                    sender.sendMessage(getLanguageBundle()[confirmNeeded])
                    sender.sendMessage("")
                    return@onCommand true
                }
                deleteConfirm.remove(requiresPlayer().uniqueId)
                saveManager.delete(finalSaveName)
                sender.sendMessage("")
                sender.sendMessage(getLanguageBundle()[success].format(finalSaveName))
                sender.sendMessage("")
                true
            }
        }
        subCommand("list", "列出所有可以載入的世界") {
            val header = getCommandLanguageKey("HEADER", "\n${ChatColor.GRAY}- 除存清單")
            val footer = getCommandLanguageKey("FOOTER", "")
            val clickToTeleport = getCommandLanguageKey("CLICK_TO_TELEPORT", "${ChatColor.YELLOW}[點我載入]")
            val clickToDelete = getCommandLanguageKey("CLICK_TO_TELEPORT", "${ChatColor.RED}  [點我刪除]")
            val loaded = getCommandLanguageKey("LOADED", "已經匯入")
            val autoSave = getCommandLanguageKey("AUTO_SAVE", "[自動除存]")
            onCommand {
                Thread {
                    blockingDbQuery(saveManager.database) {
                        sender.sendMessage(getLanguageBundle()[header])
                        for (resultRow in TableWorldSaves.selectAll()) {
                            val saveName = resultRow[TableWorldSaves.saveName]
                            TextComponent()
                            val autoSaveText =  if (resultRow[TableWorldSaves.autoSave]) " " + getLanguageBundle()[autoSave] + " " else ""
                            if (Bukkit.getWorld(saveName) == null) {
                                val command = "/backup load $saveName"
                                requiresPlayer().spigot().sendMessage(TextComponent(" ${ChatColor.GRAY}- ${ChatColor.GREEN}$autoSaveText$saveName    ").also {
                                    it.addExtra(TextComponent(getLanguageBundle()[clickToTeleport]).also { button ->
                                        button.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, command)
                                        button.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, arrayOf(TextComponent(getLanguageBundle()[hoverText].format(command))))
                                    })
                                }.also {
                                    val deleteCommand = "/backup delete $saveName"
                                    it.addExtra(TextComponent(getLanguageBundle()[clickToDelete]).also { button ->
                                        button.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, deleteCommand)
                                        button.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, arrayOf(TextComponent(getLanguageBundle()[hoverText].format(deleteCommand))))
                                    })
                                })
                            } else {
                                requiresPlayer().spigot().sendMessage(TextComponent(" ${ChatColor.GRAY}- ${ChatColor.GREEN}$autoSaveText$saveName    " + getLanguageBundle()[loaded]).also {
                                    val deleteCommand = "/backup delete $saveName"
                                    it.addExtra(TextComponent(getLanguageBundle()[clickToDelete]).also { button ->
                                        button.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, deleteCommand)
                                        button.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, arrayOf(TextComponent(getLanguageBundle()[hoverText].format(deleteCommand))))
                                    })
                                })
                            }

                        }
                        sender.sendMessage(getLanguageBundle()[footer])
                    }
                }.start()
                true
            }
        }

        subCommand("help", "查看世界存檔的指令列表") {
            val header = getCommandLanguageKey("HEADER", "\n${ChatColor.GREEN}========== 世界存檔 (/backup) ==========")
            onCommand {
                sender.sendMessage(getLanguageBundle()[header])

                for (subCommand in parent.subCommands) {
                    sender.sendMessage("${ChatColor.YELLOW} /backup ${subCommand.name} ${subCommand.getUsageString()}  -  ${ChatColor.AQUA}${(subCommand as BukkitSubCommand).getDescription(getLanguageBundle())}")
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