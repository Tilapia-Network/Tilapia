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
class PacketReceiveEvent(original: PacketEvent): TilapiaPacketEvent(original) {


}