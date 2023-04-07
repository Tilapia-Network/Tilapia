package net.tilapia.lobby.main

import net.tilapia.lobby.TilapiaLobbyPlugin
import org.bukkit.plugin.java.JavaPlugin

class Main: JavaPlugin() {

    override fun onEnable() {
        TilapiaLobbyPlugin.plugin = this
    }

    override fun onDisable() {

    }

}