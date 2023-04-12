package net.tilapiamc.multiworld

import org.bukkit.World
import org.bukkit.WorldType

data class TilapiaWorld(val name: String, val worldType: WorldType, val generator: String?, val generatorParameters: String?) {

}