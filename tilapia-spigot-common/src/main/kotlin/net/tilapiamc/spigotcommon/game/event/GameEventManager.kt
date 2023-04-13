package net.tilapiamc.spigotcommon.game.event

import net.tilapiamc.api.events.EventsManager
import net.tilapiamc.api.events.annotation.registerAnnotationBasedListener
import net.tilapiamc.api.events.annotation.unregisterAnnotationBasedListener
import net.tilapiamc.spigotcommon.game.LocalGame

class GameEventManager(val game: LocalGame) {

    val listeners = ArrayList<Any>()

    fun registerListener(listener: Any, includeEventSubclasses: Boolean = true) {
        listeners.add(listener)
        EventsManager.registerAnnotationBasedListener(listener, includeEventSubclasses) {
            game.shouldHandleEvent(it)
        }
    }

    fun unregisterListener(listener: Any) {
        listeners.remove(listener)
        EventsManager.unregisterAnnotationBasedListener(listener)
    }
    fun unregisterAll() {
        for (listener in listeners) {
            EventsManager.unregisterAnnotationBasedListener(listener)
        }
        listeners.clear()
    }

}