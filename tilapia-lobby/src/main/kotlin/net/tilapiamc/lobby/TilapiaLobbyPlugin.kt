package net.tilapiamc.lobby

import net.tilapiamc.api.TilapiaCore
import net.tilapiamc.lobby.main.Main
import org.bukkit.Bukkit

object TilapiaLobbyPlugin {

    lateinit var plugin: Main

    fun onEnable() {
        net.tilapiamc.api.TilapiaCore.instance.addGame(TilapiaLobby(net.tilapiamc.api.TilapiaCore.instance, Bukkit.getWorld("world")))
    }

    fun onDisable() {

    }

}