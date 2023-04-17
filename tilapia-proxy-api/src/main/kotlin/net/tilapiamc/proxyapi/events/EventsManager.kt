package net.tilapiamc.proxyapi.events

import com.velocitypowered.api.proxy.ProxyServer
import net.tilapiamc.common.events.AbstractEventsManager
import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner
import org.slf4j.LoggerFactory
import java.lang.reflect.Modifier

typealias AbstractEvent = Any

class EventsManager(val plugin: Any, val proxy: ProxyServer): AbstractEventsManager() {


    val logger = LoggerFactory.getLogger("EventsManager")
    private val listening = ArrayList<Class<out Any>>()

    init {
        val reflections = Reflections("com.velocitypowered.api.event", SubTypesScanner(false))


        for (clazz in reflections.getSubTypesOf(Any::class.java)) {
            if (clazz.name.endsWith("Event") && !Modifier.isAbstract(clazz.modifiers)) { // Valid Spigot Event
                listenForEvent(clazz)
            }
        }
    }


    fun listenForEvent(eventType: Class<out Any>) {
        if (eventType in listening) {
            logger.warn(IllegalArgumentException("Event ${eventType.simpleName} has already been listened! Ignoring...").stackTraceToString())
            return
        }
        listening.add(eventType)
        logger.debug("Listening for event ${eventType.simpleName}")
        proxy.eventManager.register(plugin, eventType) { this(it) }
    }

}