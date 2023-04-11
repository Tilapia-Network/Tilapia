package net.tilapiamc.utilcommands

import net.tilapiamc.api.TilapiaCore
import net.tilapiamc.api.commands.SpigotCommandsManager
import net.tilapiamc.utilcommands.commands.CommandPlugins
import net.tilapiamc.utilcommands.commands.gamemode.*
import org.bukkit.plugin.java.JavaPlugin

class UtilCommands: JavaPlugin() {


    override fun onEnable() {
        SpigotCommandsManager.registerCommand(CommandPlugins())
        SpigotCommandsManager.registerCommand(CommandGmc())
        SpigotCommandsManager.registerCommand(CommandGma())
        SpigotCommandsManager.registerCommand(CommandGmspec())
        SpigotCommandsManager.registerCommand(CommandGms())
        SpigotCommandsManager.registerCommand(CommandGameMode())
    }

    override fun onDisable() {

    }
}