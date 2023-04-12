package net.tilapiamc.multiworld

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import org.apache.logging.log4j.LogManager
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.FileReader

object WorldManager {

    val worlds = HashMap<TilapiaWorld, World>()
    val registeredWorlds = ArrayList<TilapiaWorld>()

    private val gson = GsonBuilder()
        .create()

    private val logger = LogManager.getLogger("MultiWorld")
    private val worldsFile = File(JavaPlugin.getPlugin(MultiWorld::class.java).dataFolder, "worlds.json")
    fun save() {
        logger.debug("Saving worlds...")
        val out = JsonArray()
        for (registeredWorld in registeredWorlds) {
            val toJsonTree = gson.toJsonTree(registeredWorld)
            logger.debug("Saved world: $toJsonTree")
            out.add(toJsonTree)
        }
        worldsFile.writeText(gson.toJson(out))
    }
    fun load() {
        logger.debug("Loading worlds...")
        registeredWorlds.clear()
        val array = gson.fromJson(
            FileReader(worldsFile),
            JsonArray::class.java
        ) ?: JsonArray()
        for (jsonElement in array) {
            if (!jsonElement.isJsonObject) return
            val world = gson.fromJson(jsonElement, TilapiaWorld::class.java)
            registeredWorlds.add(world)
            logger.debug("Registered world: ${jsonElement.toString()}")
        }
    }

    fun createWorld(world: TilapiaWorld, seed: Int? = null) {
        val worldCreator = WorldCreator.name(world.name)
        worldCreator.generator(world.generator)
        Bukkit.getServer().createWorld(worldCreator)
    }

}