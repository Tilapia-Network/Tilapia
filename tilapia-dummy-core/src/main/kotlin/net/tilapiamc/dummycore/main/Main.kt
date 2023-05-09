package net.tilapiamc.dummycore.main

import net.tilapiamc.api.TilapiaCore
import net.tilapiamc.api.TilapiaPlugin
import net.tilapiamc.dummycore.TilapiaCoreImpl
import net.tilapiamc.ranks.RanksManager
import org.bukkit.plugin.java.JavaPlugin

class Main: TilapiaPlugin() {

    init {
        requireSchemaAccess(RanksManager.DATABASE_NAME)
        requireSchemaAccess("logs")
    }

    override fun onLoad() {
        logger.info("Tilapia Core has been loaded")
    }

    override fun onEnable() {
        val instance = TilapiaCoreImpl()
        TilapiaCore.instance = instance
        (TilapiaCore.instance as TilapiaCoreImpl).onEnable()
        logger.info("Tilapia Core has been initialized")
    }

    override fun onDisable() {
        val instance = TilapiaCore.instance as TilapiaCoreImpl
        instance.onDisable()
    }
}