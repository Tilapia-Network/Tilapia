package net.tilapia.core.main

import net.tilapia.api.TilapiaCore
import net.tilapia.core.TilapiaCoreImpl
import org.bukkit.plugin.java.JavaPlugin

class Main: JavaPlugin() {

    override fun onEnable() {
        TilapiaCore.instance = TilapiaCoreImpl()
        logger.info("Tilapia Core has been initialized")
    }
}