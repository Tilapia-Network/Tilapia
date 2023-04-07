package net.tilapia.spigotcommon.game.minigame

import net.tilapia.api.TilapiaCore
import net.tilapia.api.events.EventsManager
import net.tilapia.api.events.annotation.registerAnnotationBasedListener
import net.tilapia.api.events.annotation.unregisterAnnotationBasedListener
import net.tilapia.api.game.lobby.ManagedLobby
import net.tilapia.api.game.minigame.ManagedMiniGame
import net.tilapia.spigotcommon.game.minigame.stage.MiniGameStage
import net.tilapia.spigotcommon.game.minigame.stage.impl.StageWaiting

abstract class LocalMiniGame(core: TilapiaCore, defaultStage: MiniGameStage): ManagedMiniGame(core) {

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