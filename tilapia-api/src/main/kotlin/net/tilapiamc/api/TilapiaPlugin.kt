package net.tilapiamc.api

import org.bukkit.plugin.java.JavaPlugin

abstract class TilapiaPlugin: JavaPlugin() {

    val schemaAccess = ArrayList<String>()

    fun requireSchemaAccess(table: String) {
        schemaAccess.add(table)
    }

}