package net.tilapiamc.api.events.packet

import com.comphenix.packetwrapper.AbstractPacket
import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.events.PacketEvent
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent
import org.reflections.Reflections

abstract class TilapiaPacketEvent(val original: PacketEvent): PlayerEvent(original.player), Cancellable {

    companion object {
        val handlerList = HandlerList()
        val packetTypes: HashMap<PacketType, Class<out AbstractPacket>> by lazy {
            val reflections = Reflections("com.comphenix.packetwrapper")
            val out = HashMap<PacketType, Class<out AbstractPacket>>()
            for (clazz in reflections.getSubTypesOf(AbstractPacket::class.java)) {
                try {
                    val typeField = clazz.declaredFields.firstOrNull { it.name == "TYPE" }
                    if (typeField != null) {
                        out[typeField[null] as PacketType] = clazz
                    }
                } catch (e: Throwable) {}
            }
            out
        }
    }

    val packet: AbstractPacket? by lazy {
        val constructor = packetTypes[original.packetType]?.constructors
            ?.firstOrNull { it.parameterTypes.size == 1 && it.parameterTypes[0] == PacketContainer::class.java }
        constructor?.newInstance(original.packet) as AbstractPacket?
    }


    override fun getHandlers(): HandlerList {
        return handlerList
    }

    override fun isCancelled(): Boolean {
        return original.isCancelled
    }

    override fun setCancelled(cancel: Boolean) {
        original.isCancelled = cancel
    }

}