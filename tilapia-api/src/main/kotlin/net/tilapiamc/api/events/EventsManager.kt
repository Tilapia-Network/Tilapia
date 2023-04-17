package net.tilapiamc.api.events

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.injector.packet.PacketRegistry
import me.fan87.plugindevkit.PluginInstanceGrabber
import net.tilapiamc.api.events.packet.PacketReceiveEvent
import net.tilapiamc.api.events.packet.PacketSendEvent
import net.tilapiamc.common.events.AbstractEventsManager
import org.apache.logging.log4j.LogManager
import org.bukkit.Bukkit
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.reflections.Reflections

typealias AbstractEvent = Any

object EventsManager: AbstractEventsManager(), Listener {
    private val listening = ArrayList<Class<out Event>>()
    val logger = LogManager.getLogger("EventsManager")

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

    @JvmStatic
    fun fireEvent(event: AbstractEvent) {
        for (listener in ArrayList(listeners)) {
            listener(event)
        }
    }

}
