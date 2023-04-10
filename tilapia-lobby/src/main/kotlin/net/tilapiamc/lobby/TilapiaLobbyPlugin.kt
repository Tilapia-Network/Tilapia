package net.tilapiamc.lobby

import net.tilapiamc.api.TilapiaCore
import net.tilapiamc.lobby.main.Main
import org.bukkit.Bukkit

object TilapiaLobbyPlugin {

    lateinit var plugin: Main

    fun onEnable() {
        TilapiaCore.instance.addGame(TilapiaLobby(TilapiaCore.instance, Bukkit.getWorld("world"), "main"))
    }

    fun onDisable() {

    }

}