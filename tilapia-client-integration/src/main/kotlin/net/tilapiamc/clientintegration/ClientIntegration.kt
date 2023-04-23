package net.tilapiamc.clientintegration

import net.tilapiamc.api.commands.SpigotCommandsManager
import net.tilapiamc.clientintegration.commands.CommandNoClip
import org.bukkit.plugin.java.JavaPlugin

class ClientIntegration: JavaPlugin() {


    override fun onEnable() {
        SpigotCommandsManager.registerCommand(CommandNoClip())
    }

    override fun onDisable() {

    }
}