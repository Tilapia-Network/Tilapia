package net.tilapiamc.sandbox

import net.tilapiamc.api.commands.SpigotCommandsManager
import net.tilapiamc.sandbox.commands.CommandSandbox
import net.tilapiamc.sandbox.main.Main

object TilapiaSandboxPlugin {

    lateinit var plugin: Main

    fun onEnable() {
        TilapiaSandbox.SANDBOX_BOSS_BAR
        SpigotCommandsManager.registerCommand(CommandSandbox())
    }

    fun onDisable() {

    }

}