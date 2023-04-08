package net.tilapia.lobby

import net.tilapia.api.TilapiaCore
import net.tilapia.lobby.main.Main
import org.bukkit.Bukkit

object TilapiaLobbyPlugin {

    lateinit var plugin: Main

    fun onEnable() {
        TilapiaCore.instance.addGame(TilapiaLobby(TilapiaCore.instance, Bukkit.getWorld("world")))
    }

    fun onDisable() {

    }

}