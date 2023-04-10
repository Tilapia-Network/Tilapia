package net.tilapiamc.core.main

import net.tilapiamc.api.TilapiaCore
import net.tilapiamc.core.TilapiaCoreImpl
import org.bukkit.plugin.java.JavaPlugin

class Main: JavaPlugin() {

    override fun onEnable() {
        TilapiaCore.instance = TilapiaCoreImpl()
        logger.info("Tilapia Core has been initialized")
    }

    override fun onDisable() {
        val instance = TilapiaCore.instance as TilapiaCoreImpl
        instance.onDisable()
    }
}