package net.tilapiamc.fleetwars.main

import net.tilapiamc.api.TilapiaPlugin
import net.tilapiamc.api.commands.SpigotCommandsManager
import net.tilapiamc.fleetwars.FleetWarsLanguage
import net.tilapiamc.fleetwars.GuiShop
import net.tilapiamc.fleetwars.commands.FleetWarsCommand
import net.tilapiamc.fleetwars.config.FleetWarsConfig

class Main: TilapiaPlugin() {

    init {
        requireSchemaAccess("FleetWars")
    }

    override fun onEnable() {
        FleetWarsConfig.reload()
        SpigotCommandsManager.registerCommand(FleetWarsCommand())

        GuiShop.registerLanguageKeys()
        FleetWarsLanguage.registerLanguageKeys()
    }
}