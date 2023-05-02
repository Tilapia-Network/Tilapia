package net.tilapiamc.clientintegration.debugmenu

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import net.minecraft.server.v1_8_R3.PacketDataSerializer
import net.minecraft.server.v1_8_R3.PacketPlayOutCustomPayload
import net.tilapiamc.clientintegration.ClientIntegrationPermissions
import net.tilapiamc.common.events.annotation.Subscribe
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class Bukkit1_8DebugMenuSender: DebugMenuSender, Listener {
    override val adapters: MutableList<DebugToStringAdapter> = ArrayList()

    private val currentDebugInfo = HashMap<String, String>()

    override fun setDebugInfo(infoName: String, value: Any) {

        val stringValue = toString(value)
        if (currentDebugInfo[infoName] == stringValue) return
        currentDebugInfo[infoName] = stringValue

        for (onlinePlayer in Bukkit.getOnlinePlayers()) {
            updateDebugInfo(onlinePlayer)
        }
    }

    override fun removeDebugInfo(infoName: String) {
        if (infoName !in currentDebugInfo) return
        currentDebugInfo.remove(infoName)

        for (onlinePlayer in Bukkit.getOnlinePlayers()) {
            updateDebugInfo(onlinePlayer)
        }
    }

    @Subscribe("sendDebugMenu", mustRunAfter = ["playerJoinInit"]) // Ensures the player data has been loaded
    fun onJoin(event: PlayerJoinEvent) {
        updateDebugInfo(event.player)
    }

    fun updateDebugInfo(player: Player) {
        if (!player.hasPermission(ClientIntegrationPermissions.DEBUG_MENU)) {
            return
        }
        // Average MCP user:
        val friendlyByteBuffer = PacketDataSerializer(Unpooled.buffer())
        friendlyByteBuffer.writeInt(currentDebugInfo.size)
        for (mutableEntry in currentDebugInfo) {
            friendlyByteBuffer.a(mutableEntry.key)
            friendlyByteBuffer.a(mutableEntry.value)
        }
        (player as CraftPlayer).handle.playerConnection.sendPacket(PacketPlayOutCustomPayload("Tilapia|DebugMenu", friendlyByteBuffer))
    }

}