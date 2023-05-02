package net.tilapiamc.clientintegration

import net.tilapiamc.api.commands.SpigotCommandsManager
import net.tilapiamc.api.events.EventsManager
import net.tilapiamc.clientintegration.commands.CommandNoClip
import net.tilapiamc.clientintegration.debugmenu.Bukkit1_8DebugMenuSender
import net.tilapiamc.clientintegration.debugmenu.DebugMenuSender
import net.tilapiamc.common.events.annotation.registerAnnotationBasedListener
import org.bukkit.plugin.java.JavaPlugin

class ClientIntegration: JavaPlugin() {

    companion object {
        val debugMenu: DebugMenuSender = Bukkit1_8DebugMenuSender()
    }

    override fun onEnable() {
        ClientIntegrationPermissions // Register permissions
        SpigotCommandsManager.registerCommand(CommandNoClip())

        EventsManager.registerAnnotationBasedListener(debugMenu)
    }

    override fun onDisable() {

    }
}