package net.tilapiamc.api.generators.impl

import net.tilapiamc.api.generators.AbstractGenerator
import org.apache.logging.log4j.LogManager
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.Biome
import java.util.*

class GeneratorVoid(parameter: String?): AbstractGenerator("Void", parameter) {

    val logger = LogManager.getLogger("VoidGen")
    val biome: Biome

    init {
        val target = Biome.values().firstOrNull { it.name.lowercase() == parameter?.lowercase() }
        if (target == null) {
            logger.info("Invalid biome: $parameter, using PLAINS")
        }
        biome = target ?: Biome.PLAINS
    }

    override fun getFixedSpawnLocation(world: World?, random: Random?): Location {
        return Location(world, 0.0, 0.0, 0.0)
    }

    override fun generateChunkData(world: World?, random: Random?, x: Int, z: Int, biome: BiomeGrid): ChunkData {
        val chunkData = createChunkData(world)
        for (x in 0..15) {
            for (z in 0..15) {
                biome.setBiome(x, z, this.biome)
            }
        }
        if (x == 0 && z == 0) {
            chunkData.setRegion(0, 0, 0, 16, 1, 16, Material.BEDROCK)
        }
        return chunkData
    }
}