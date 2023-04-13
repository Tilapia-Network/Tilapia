package net.tilapiamc.sandbox

import net.tilapiamc.api.TilapiaCore
import net.tilapiamc.api.commands.SpigotCommandsManager
import net.tilapiamc.sandbox.commands.CommandSandbox
import net.tilapiamc.sandbox.main.Main
import org.bukkit.Bukkit

object TilapiaSandboxPlugin {

    lateinit var plugin: Main

    fun onEnable() {
        SpigotCommandsManager.registerCommand(CommandSandbox())
    }

    fun onDisable() {

    }

}