package net.tilapiamc.fleetwars.main

import net.tilapiamc.api.TilapiaPlugin
import net.tilapiamc.api.commands.SpigotCommandsManager
import net.tilapiamc.fleetwars.commands.FleetWarsCommand

class Main: TilapiaPlugin() {

    init {
        requireSchemaAccess("FleetWars")
    }

    override fun onEnable() {
        SpigotCommandsManager.registerCommand(FleetWarsCommand())
    }
}