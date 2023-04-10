package net.tilapiamc.fleetwars.main

import net.tilapiamc.api.TilapiaCore
import net.tilapiamc.api.commands.SpigotCommandsManager
import net.tilapiamc.api.game.GamesManager
import net.tilapiamc.fleetwars.FleetWars
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class Main: JavaPlugin() {

    override fun onEnable() {
        GamesManager.registerManagedGame(FleetWars(TilapiaCore.instance, Bukkit.getWorld("world_the_end")))
    }
}