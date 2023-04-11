package net.tilapiamc.api.events

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.injector.PacketFilterManager
import com.comphenix.protocol.injector.packet.PacketRegistry
import me.fan87.plugindevkit.PluginInstanceGrabber
import net.tilapiamc.api.events.packet.PacketReceiveEvent
import net.tilapiamc.api.events.packet.PacketSendEvent
import org.apache.logging.log4j.LogManager
import org.bukkit.Bukkit
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.reflections.Reflections

typealias AbstractEvent = Any

object EventsManager: Listener {
    val logger = LogManager.getLogger("EventsManager")
    private val listening = ArrayList<Class<out Event>>()

    init {
        val reflections = Reflections("org.bukkit.event")

        for (clazz in reflections.getSubTypesOf(Event::class.java)) {
            if (clazz.declaredMethods.any { it.name == "getHandlerList" }) { // Valid Spigot Event
                listenForEvent(clazz)
            }
        }

        ProtocolLibrary.getProtocolManager().addPacketListener(object : PacketAdapter(PluginInstanceGrabber.getPluginInstance(), PacketType.values().filter {
            PacketRegistry.getServerPacketTypes().contains(it) || PacketRegistry.getClientPacketTypes().contains(it)
        }) {
            override fun onPacketSending(event: PacketEvent) {
                fireEvent(PacketSendEvent(event))
            }

            override fun onPacketReceiving(event: PacketEvent) {
                fireEvent(PacketReceiveEvent(event))
            }
        })

    }


    fun listenForEvent(eventType: Class<out Event>) {
        if (eventType in listening) {
            logger.warn(IllegalArgumentException("Event ${eventType.simpleName} has already been listened! Ignoring...").stackTraceToString())
            return
        }
        listening.add(eventType)
        logger.debug("Listening for event ${eventType.simpleName}")
        Bukkit.getServer().pluginManager.registerEvent(eventType, this, EventPriority.NORMAL, { listener, event ->
            fireEvent(event)
        }, PluginInstanceGrabber.getPluginInstance())
    }

    val listenersByName = LinkedHashMap<String, EventListener>()
    val listeners = ArrayList<EventListener>()

    fun registerListener(listener: EventListener) {
        synchronized(listeners) {
            listenersByName[listener.name] = listener
            listeners.add(listener)
            sortListeners()
        }
    }

    fun unregisterListener(listener: EventListener) {
        synchronized(listeners) {
            listenersByName.remove(listener.name)
            listeners.remove(listener)
        }

    }

    private fun sortListeners() {
        val comparator = ListenerComparator()
        listeners.sortWith(comparator)
    }

    @JvmStatic
    fun fireEvent(event: AbstractEvent) {
        for (listener in ArrayList(listeners)) {
            listener(event)
        }
    }

}

class ListenerComparator : Comparator<EventListener> {
    private val visited: MutableSet<String>
    private val recursionStack: MutableSet<String>

    init {
        visited = HashSet()
        recursionStack = HashSet()
    }

    override fun compare(obj1: EventListener, obj2: EventListener): Int {
        visited.clear()
        recursionStack.clear()

        if (hasCycle(obj1, obj2)) {
            error("Circular dependency detected")
        }

        if (obj1.mustRunAfter.contains(obj2.name)) {
            return 1
        }
        if (obj2.mustRunAfter.contains(obj1.name)) {
            return -1
        }
        if (obj1.mustRunBefore.contains(obj2.name)) {
            return -1
        }
        return if (obj2.mustRunBefore.contains(obj1.name)) {
            1
        } else obj1.name.compareTo(obj2.name)

    }

    private fun hasCycle(obj1: EventListener, obj2: EventListener): Boolean {
        visited.add(obj1.name)
        recursionStack.add(obj1.name)

        for (name in obj1.mustRunAfter) {
            if (!visited.contains(name)) {
                val next = EventsManager.listenersByName[name]
                if (next != null && hasCycle(next, obj2)) {
                    return true
                }
            } else if (recursionStack.contains(name)) {
                return true
            }
        }

        recursionStack.remove(obj1.name)
        return false
    }
}