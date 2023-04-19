package net.tilapiamc.gameextension.plugins

import me.fan87.plugindevkit.events.EntityTickEvent
import net.minecraft.server.v1_8_R3.EntityTrackerEntry
import net.minecraft.server.v1_8_R3.EntityWither
import net.tilapiamc.api.events.game.PlayerJoinGameEvent
import net.tilapiamc.api.events.game.PlayerQuitGameEvent
import net.tilapiamc.api.player.LocalNetworkPlayer
import net.tilapiamc.api.player.PlayersManager.getLocalPlayer
import net.tilapiamc.common.events.annotation.Subscribe
import net.tilapiamc.spigotcommon.game.plugin.GamePlugin
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerRespawnEvent

class PluginBossBar1_8_8(val getBossBarText: (LocalNetworkPlayer) -> String?): GamePlugin() {


    val bossBarForPlayer = HashMap<Player, EntityTrackerEntry>()

    override fun onEnable() {
        eventManager.registerListener(this)
    }

    override fun onDisable() {
    }

    @Subscribe("bossBar-onPlayerJoin")
    fun onPlayerJoin(event: PlayerJoinGameEvent) {
    }

    @Subscribe("bossBar-onPlayerTick")
    fun onPlayerTick(event: EntityTickEvent) {
        val player = event.entity
        if (player !is Player) return
        try {
            val text = getBossBarText(player.getLocalPlayer())
            if (text != null) {
                if (bossBarForPlayer[player] != null) {
                    bossBarForPlayer[player]!!.tracker.customName = text
                    bossBarForPlayer[player]!!.tracker.isInvisible = true
                    val location = getNPCLocation(player)
                    bossBarForPlayer[player]!!.tracker.setPositionRotation(location.x, location.y, location.z, 0f, 0f)
                    bossBarForPlayer[player]!!.track(listOf((player as CraftPlayer).handle))
                } else {
                    val npc = createNPC(player)
                    bossBarForPlayer[player] = npc
                    npc.tracker.customName = text
                    npc.track(listOf((player as CraftPlayer).handle))
                }
            }
        } catch (e: NullPointerException) {}
    }

    @Subscribe("bossBar-onRespawn")
    fun onRespawn(event: PlayerRespawnEvent) {
        bossBarForPlayer[event.player]?.a((event.player as CraftPlayer).handle)
        bossBarForPlayer.remove(event.player)
    }

    @Subscribe("bossBar-onPlayerQuit")
    fun onPlayerQuit(event: PlayerQuitGameEvent) {
        bossBarForPlayer[event.player.bukkitPlayer]?.a((event.player.bukkitPlayer as CraftPlayer).handle)
        bossBarForPlayer.remove(event.player.bukkitPlayer)
    }

    fun createNPC(player: Player): EntityTrackerEntry {
        val npc = EntityWither((player as CraftPlayer).handle.world)
        val tracker = EntityTrackerEntry(npc, 128, 1, false)
        return tracker
    }

    fun getNPCLocation(player: Player): Location {
        val vec = player.location.toVector()
        vec.add(player.location.direction.multiply(20))
        return Location(player.world, vec.x, vec.y, vec.z)
    }



}