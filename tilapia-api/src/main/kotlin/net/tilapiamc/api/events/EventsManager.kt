package net.tilapiamc.api.events

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.injector.packet.PacketRegistry
import com.google.common.reflect.ClassPath
import me.fan87.plugindevkit.PluginInstanceGrabber
import net.citizensnpcs.api.event.NPCCollisionEvent
import net.citizensnpcs.api.event.NPCEvent
import net.tilapiamc.api.events.packet.PacketReceiveEvent
import net.tilapiamc.api.events.packet.PacketSendEvent
import net.tilapiamc.common.events.AbstractEvent
import net.tilapiamc.common.events.AbstractEventsManager
import org.apache.logging.log4j.LogManager
import org.bukkit.Bukkit
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.reflections.Reflections
import org.reflections.util.ConfigurationBuilder
import java.io.DataInputStream
import java.io.InputStream

typealias AbstractEvent = Any

object EventsManager: AbstractEventsManager(), Listener {
    private val listening = ArrayList<Class<out Event>>()
    val logger = LogManager.getLogger("EventsManager")

    init {


        listenInPackage("org.bukkit.event")
        listenInPackage("org.spigotmc.event")
        // For some reason, reflections isn't detecting citizens events
        ClassPath.from(NPCEvent::class.java.classLoader).getTopLevelClasses("net.citizensnpcs.api.event").forEach {

            val clazz = Class.forName(it.name)
            if (Event::class.java.isAssignableFrom(clazz)) {
                if (clazz.declaredMethods.any { it.name == "getHandlerList" }) { // Valid Spigot Event
                    listenForEvent(clazz as Class<out Event>)
                }
            }
        }

    }

    fun listenInPackage(packageName: String) {

        val reflections = Reflections(packageName)

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

    override fun invoke(event: AbstractEvent) {
        fireEvent(event)
    }

    @JvmStatic
    fun fireEvent(event: AbstractEvent) {
        for (listener in ArrayList(listeners)) {
            try {
                listener(event)
            } catch (e: Throwable) {
                logger.error("Could not pass event ${event.javaClass.simpleName}")
                e.printStackTrace()
            }
        }
    }

}
