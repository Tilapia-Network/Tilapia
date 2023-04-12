package net.tilapiamc.api.generators

import org.bukkit.generator.ChunkGenerator

abstract class AbstractGenerator(val name: String, val parameter: String?): ChunkGenerator() {
}