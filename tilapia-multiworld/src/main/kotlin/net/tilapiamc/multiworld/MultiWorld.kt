package net.tilapiamc.multiworld

import net.tilapiamc.api.TilapiaCore
import net.tilapiamc.api.commands.BukkitCommand
import net.tilapiamc.api.commands.SpigotCommandsManager
import net.tilapiamc.multiworld.subcommands.commandHelp
import org.bukkit.plugin.java.JavaPlugin


class MultiWorld: JavaPlugin() {


    override fun onEnable() {
        SpigotCommandsManager.registerCommand(MultiWorldCommand())
    }

    override fun onDisable() {

    }
}


class MultiWorldCommand: BukkitCommand("multiworld", "多世界插件的主要指令", true) {
    init {
        addAlias("mv")
        addAlias("mw")
        addAlias("multiverse")

        subCommand("help", "顯示多世界插件的所有指令", commandHelp())
        onCommand {
            subCommands.first { it.name == "help" }.execute(commandAlias, sender, arrayOf("help", *rawArgs))
            true
        }
    }
}

