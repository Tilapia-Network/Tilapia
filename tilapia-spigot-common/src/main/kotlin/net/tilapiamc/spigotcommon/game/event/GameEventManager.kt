package net.tilapiamc.spigotcommon.game.event

import net.tilapiamc.api.events.EventsManager
import net.tilapiamc.api.events.annotation.registerAnnotationBasedListener
import net.tilapiamc.api.events.annotation.unregisterAnnotationBasedListener
import net.tilapiamc.spigotcommon.game.LocalGame

class GameEventManager(val game: LocalGame) {

    fun registerListener(listener: Any, includeEventSubclasses: Boolean = true) {
        EventsManager.registerAnnotationBasedListener(listener, includeEventSubclasses) {
            game.shouldHandleEvent(it)
        }
    }

    fun unregisterListener(listener: Any) {
        EventsManager.unregisterAnnotationBasedListener(listener)
    }

}