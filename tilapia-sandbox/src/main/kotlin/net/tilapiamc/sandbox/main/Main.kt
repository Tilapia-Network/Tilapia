package net.tilapiamc.sandbox.main

import net.tilapiamc.sandbox.TilapiaSandboxPlugin
import org.bukkit.plugin.java.JavaPlugin

class Main: JavaPlugin() {

    override fun onEnable() {
        TilapiaSandboxPlugin.plugin = this
        TilapiaSandboxPlugin.onEnable()
    }

    override fun onDisable() {
        TilapiaSandboxPlugin.onDisable()
    }

}