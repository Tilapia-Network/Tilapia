package net.tilapiamc.spigotcommon.game.minigame

import net.tilapiamc.api.TilapiaCore
import net.tilapia.api.events.EventsManager
import net.tilapia.api.events.annotation.registerAnnotationBasedListener
import net.tilapia.api.events.annotation.unregisterAnnotationBasedListener
import net.tilapia.api.game.lobby.ManagedLobby
import net.tilapia.api.game.minigame.ManagedMiniGame
import net.tilapiamc.spigotcommon.game.AbstractRule
import net.tilapiamc.spigotcommon.game.LocalGame
import net.tilapiamc.spigotcommon.game.minigame.stage.MiniGameStage
import net.tilapiamc.spigotcommon.game.minigame.stage.impl.StageWaiting
import org.bukkit.World

abstract class LocalMiniGame(core: net.tilapiamc.api.TilapiaCore, defaultStage: MiniGameStage, gameWorld: World): ManagedMiniGame(core, gameWorld),
    LocalGame {
    override val rules = ArrayList<AbstractRule>()

    var currentStage: MiniGameStage = defaultStage
        set(value) {
            EventsManager.unregisterAnnotationBasedListener(field)
            EventsManager.registerAnnotationBasedListener(value)
            field = value
        }

    init {
        EventsManager.registerAnnotationBasedListener(defaultStage)
    }

}