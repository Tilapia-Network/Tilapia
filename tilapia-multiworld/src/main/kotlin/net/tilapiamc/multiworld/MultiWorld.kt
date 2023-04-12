package net.tilapiamc.multiworld

import net.tilapiamc.api.commands.*
import net.tilapiamc.multiworld.args.GeneratorNotFoundException
import net.tilapiamc.multiworld.args.IllegalWorldNameException
import net.tilapiamc.multiworld.args.WorldNotFoundException
import net.tilapiamc.multiworld.subcommands.*
import org.bukkit.ChatColor
import org.bukkit.plugin.java.JavaPlugin


class MultiWorld: JavaPlugin() {


    override fun onEnable() {
        SpigotCommandsManager.registerCommand(MultiWorldCommand())
        WorldManager.load()
    }

    override fun onDisable() {

    }
}


class MultiWorldCommand: BukkitCommand("multiworld", "多世界插件的主要指令", true) {
    init {
        val worldNotLoaded = getCommandLanguageKey("ERROR_WORLD_NOT_LOADED", "${ChatColor.RED}世界 %1\$s 並未被載入！")
        val worldNotRegistered = getCommandLanguageKey("ERROR_WORLD_NOT_REGISTERED", "${ChatColor.RED}世界 %1\$s 並未被註冊！")
        val generatorNotFound = getCommandLanguageKey("ERROR_GENERATOR_NOT_FOUND", "${ChatColor.RED}找不到名為 %1\$s 的世界生成器！")
        val illegalWorldName = getCommandLanguageKey("ERROR_ILLEGAL_WORLD_NAME", "${ChatColor.RED}%1\$s 並不是一個有效的世界名稱！")
        addAlias("mv")
        addAlias("mw")
        addAlias("multiverse")
        exceptionHandlers.add { e, sender, args ->
            if (e is WorldNotFoundException) {
                if (e.requireLoaded) {
                    sender.sendMessage(sender.getSenderLanguageBundle()[worldNotLoaded].format(e.worldName))
                } else {
                    sender.sendMessage(sender.getSenderLanguageBundle()[worldNotRegistered].format(e.worldName))
                }
                true
            } else if (e is IllegalWorldNameException) {
                sender.sendMessage(sender.getSenderLanguageBundle()[illegalWorldName].format(e.worldName))
                true
            }  else if (e is GeneratorNotFoundException) {
                sender.sendMessage(sender.getSenderLanguageBundle()[generatorNotFound].format(e.generatorName))
                true
            } else {
                false
            }
        }

        subCommand("help", "顯示多世界插件的所有指令", commandHelp())
        subCommand("list", "顯示所有已註冊的世界", commandList())
        subCommand("info", "顯示一個世界的詳細資料", commandInfo())
        subCommand("create", "創建, 註冊並載入一個世界", commandCreate())
        subCommand("import", "註冊並載入一個世界", commandImport())
        subCommand("tp", "傳送到一個載入過的世界", commandTp())
        subCommand("who", "顯示所有在指定世界的玩家", commandWho())
        subCommand("unload", "卸載一個世界", commandUnload())
        subCommand("remove", "卸載並且取消註冊一個世界", commandRemove())
        subCommand("clone", "複製並載入一個世界", commandClone())
        subCommand("clone-temporary", "複製並載入一個暫時世界", commandCloneTemporary())
        subCommand("delete", "無用的指令，請手動刪除資料夾以刪除世界") {
            val hint = getCommandLanguageKey("hint", "${ChatColor.RED}此插件因為安全原因並不支援此指令！請手動刪除世界資料夾以刪除世界")
            onCommand {
                sender.sendMessage(getLanguageBundle()[hint])
                true
            }
        }
        onCommand {
            subCommands.first { it.name == "help" }.execute(commandAlias, sender, arrayOf("help", *rawArgs))
            true
        }
    }
}

