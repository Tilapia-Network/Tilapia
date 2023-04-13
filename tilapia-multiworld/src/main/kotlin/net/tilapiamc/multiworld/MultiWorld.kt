package net.tilapiamc.multiworld

import net.tilapiamc.api.commands.*
import net.tilapiamc.api.generators.Generators
import net.tilapiamc.command.CommandException
import net.tilapiamc.multiworld.subcommands.*
import org.apache.logging.log4j.LogManager
import org.bukkit.ChatColor
import org.bukkit.plugin.java.JavaPlugin
import java.io.File


class MultiWorld: JavaPlugin() {

    val logger = LogManager.getLogger("MultiWorld")

    override fun onEnable() {
        Generators
        SpigotCommandsManager.registerCommand(MultiWorldCommand())
        WorldManager.load()

        for (registeredWorld in ArrayList(WorldManager.registeredWorlds)) {
            if (!File(registeredWorld.name).exists() || !File(registeredWorld.name).isDirectory) {
                logger.warn("World ${registeredWorld.name} is no longer found in server directory! Removing...")
                WorldManager.unregisterWorld(registeredWorld.name)
            } else {
                WorldManager.createWorld(registeredWorld)
            }
        }
    }

    override fun onDisable() {

    }
}


class MultiWorldCommand: BukkitCommand("multiworld", "多世界插件的主要指令", true) {
    val loadedRegistered = getCommandLanguageKey("LOADED_REGISTERED", "已載入")
    val registeredNotLoaded = getCommandLanguageKey("REGISTERED_NOT_LOADED", "尚未載入")
    val loadedNotRegistered = getCommandLanguageKey("LOADED_NOT_REGISTERED", "尚未註冊")
    val tempWorld = getCommandLanguageKey("TEMP_WORLD", "暫時世界")

    init {
        val worldNotLoaded = getCommandLanguageKey("ERROR_WORLD_NOT_LOADED", "${ChatColor.RED}世界 %1\$s 並未被載入！")
        val worldNotRegistered = getCommandLanguageKey("ERROR_WORLD_NOT_REGISTERED", "${ChatColor.RED}世界 %1\$s 並未被註冊！")
        val worldAlreadyRegistered = getCommandLanguageKey("WORLD_ALREADY_EXISTS", "${ChatColor.RED}名為 ${ChatColor.YELLOW}%1\$s ${ChatColor.RED}的世界早已存在")
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
            }  else if (e is WorldAlreadyExists) {
                sender.sendMessage(sender.getSenderLanguageBundle()[worldAlreadyRegistered].format(e.worldName))
                true
            } else {
                false
            }
        }

        subCommand("help", "顯示多世界插件的所有指令", commandHelp())
        subCommand("list", "顯示所有已註冊/載入的世界", commandList())
        subCommand("info", "顯示一個世界的詳細資料", commandInfo())
        subCommand("create", "創建, 註冊並載入一個世界", commandCreate())
        subCommand("import", "註冊並載入一個世界", commandImport())
        subCommand("tp", "傳送到一個載入過的世界", commandTp())
        subCommand("who", "顯示所有在指定世界的玩家", commandWho())
        subCommand("unload", "卸載一個世界", commandUnload())
        subCommand("load", "載入一個世界", commandLoad())
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



class WorldAlreadyExists(val worldName: String): CommandException(worldName)
class GeneratorNotFoundException(val generatorName: String): CommandException(generatorName)
class IllegalWorldNameException(val worldName: String): CommandException(worldName)
class WorldNotFoundException(val worldName: String, val requireRegistered: Boolean, val requireLoaded: Boolean): CommandException(worldName)