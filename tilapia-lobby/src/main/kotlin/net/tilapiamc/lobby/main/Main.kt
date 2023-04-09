package net.tilapiamc.lobby.main

import net.tilapiamc.lobby.TilapiaLobby
import net.tilapiamc.lobby.TilapiaLobbyPlugin
import org.bukkit.plugin.java.JavaPlugin

class Main: JavaPlugin() {

    override fun onEnable() {
        TilapiaLobbyPlugin.plugin = this
        TilapiaLobbyPlugin.onEnable()
    }

    override fun onDisable() {
        TilapiaLobbyPlugin.onDisable()
    }

}