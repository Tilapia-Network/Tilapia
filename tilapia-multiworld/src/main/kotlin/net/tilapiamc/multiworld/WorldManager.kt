package net.tilapiamc.multiworld

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import net.tilapiamc.api.generators.Generators
import org.apache.logging.log4j.LogManager
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.FileReader

object WorldManager {

    val registeredWorlds = ArrayList<TilapiaWorld>()

    private val gson = GsonBuilder()
        .create()

    private val logger = LogManager.getLogger("MultiWorld")
    private val worldsFile = File(JavaPlugin.getPlugin(MultiWorld::class.java).dataFolder, "worlds.json").also {
        it.parentFile.mkdirs()
        it.createNewFile()
    }
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

    fun checkName(name: String): Boolean = !(name.startsWith("temp-"))

    fun registerWorld(world: TilapiaWorld) {
        if (!checkName(world.name)) {
            throw IllegalArgumentException("Invalid world name: ${world.name}")
        }
        registeredWorlds.add(world)
        save()
    }
    fun unregisterWorld(worldName: String) {
        registeredWorlds.removeIf { it.name == worldName }
        save()
    }
    fun createWorld(world: TilapiaWorld, seed: String? = null): World {
        val worldCreator = WorldCreator.name(world.name)
        if (world.generator?.isNotEmpty() == true) {
            worldCreator.generator(Generators.generators[world.generator]?.invoke(world.generatorParameters)?:throw IllegalArgumentException("Generator named ${world.generator} is not found"))
        }
        if (world.generatorParameters?.isNotEmpty() == true) {
            worldCreator.generatorSettings(world.generatorParameters)
        }
        worldCreator.type(world.worldType)
        val theWorld = Bukkit.getServer().createWorld(worldCreator)
        return theWorld
    }

}