package net.tilapiamc.spigotcommon.utils

import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.WorldCreator

object TemporaryWorldProvider {

    init {

    }

    fun createTemporaryWorldFromWorld(world: World) {

    }

    fun provideTemporaryWorld(worldCreator: WorldCreator): World {
        val world = Bukkit.createWorld(worldCreator)
        return world
    }

}