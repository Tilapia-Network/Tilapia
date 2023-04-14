package net.tilapiamc.spigotcommon.game.plugin

import net.tilapiamc.api.TilapiaCore
import net.tilapiamc.spigotcommon.game.LocalGame
import net.tilapiamc.spigotcommon.game.event.GameEventManager

abstract class GamePlugin {

    lateinit var game: LocalGame
    lateinit var eventManager: GameEventManager
    val tilapiaCore = TilapiaCore.instance

    abstract fun onEnable()
    abstract fun onDisable()

}