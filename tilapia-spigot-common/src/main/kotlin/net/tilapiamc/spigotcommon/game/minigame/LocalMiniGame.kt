package net.tilapiamc.spigotcommon.game.minigame

import net.tilapiamc.api.TilapiaCore
import net.tilapiamc.api.events.EventsManager
import net.tilapiamc.api.events.annotation.registerAnnotationBasedListener
import net.tilapiamc.api.events.annotation.unregisterAnnotationBasedListener
import net.tilapiamc.api.game.lobby.ManagedLobby
import net.tilapiamc.api.game.minigame.ManagedMiniGame
import net.tilapiamc.spigotcommon.game.AbstractRule
import net.tilapiamc.spigotcommon.game.LocalGame
import net.tilapiamc.spigotcommon.game.event.GameEventManager
import net.tilapiamc.spigotcommon.game.minigame.stage.MiniGameStage
import net.tilapiamc.spigotcommon.game.minigame.stage.impl.StageWaiting
import net.tilapiamc.spigotcommon.game.plugin.GamePlugin
import org.bukkit.World

abstract class LocalMiniGame(core: TilapiaCore, defaultStage: MiniGameStage, gameWorld: World): ManagedMiniGame(core, gameWorld),
    LocalGame {
    override val rules = ArrayList<AbstractRule>()
    override val plugins = ArrayList<GamePlugin>()
    override val gameEventManager = GameEventManager(this)

    override fun end() {
        super.end()
        endPlugins()
    }

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