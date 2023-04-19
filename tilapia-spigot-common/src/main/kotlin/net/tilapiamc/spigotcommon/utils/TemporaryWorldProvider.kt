package net.tilapiamc.spigotcommon.utils

import org.apache.logging.log4j.LogManager
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.WorldCreator
import java.io.File
import java.util.*

object TemporaryWorldProvider {
    private val logger = LogManager.getLogger("TemporaryWorldProvider")

    init {
        File(".").listFiles()?.forEach { listFile ->
            if (Bukkit.getWorld(listFile.name) != null) {
                Bukkit.unloadWorld(listFile.name, false)
            }
            if (listFile.name.startsWith("temp-")) {
                listFile.deleteRecursively()
            }
        }

    }


    fun createTemporaryWorldFromWorld(world: World, name: String = UUID.randomUUID().toString()): World {
        val newWorldName = "temp-$name"
        logger.info("Created temporary world $newWorldName  from  ${world.name}")
        val newWorldDir = File(newWorldName)
        val oldWorldDir = File(world.name)
        if (newWorldDir.exists()) {
            newWorldDir.deleteRecursively()
        }
        newWorldDir.mkdirs()
        oldWorldDir.copyRecursively(newWorldDir, true)
        val uidDat = File(newWorldDir, "uid.dat")
        val sessionLock = File(newWorldDir, "session.lock")
        uidDat.delete()
        sessionLock.delete()
        return Bukkit.createWorld(WorldCreator.name(newWorldName).copy(world))
    }


}