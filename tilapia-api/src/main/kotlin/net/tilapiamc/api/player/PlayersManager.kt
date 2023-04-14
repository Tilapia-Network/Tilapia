package net.tilapiamc.api.player

import net.tilapiamc.api.TilapiaCore
import net.tilapiamc.api.events.EventsManager
import net.tilapiamc.api.events.annotation.Subscribe
import net.tilapiamc.api.events.annotation.registerAnnotationBasedListener
import net.tilapiamc.api.player.LocalNetworkPlayer.Companion.resetBukkitPlayer
import org.bukkit.entity.Player
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.UUID

object PlayersManager {
    init {
        EventsManager.registerAnnotationBasedListener(this)
    }

    val localPlayers = HashMap<UUID, LocalNetworkPlayer>()
    val players = HashMap<UUID, NetworkPlayer>()

    @Subscribe("playerJoinInit")
    fun onPlayerJoin(event: PlayerJoinEvent) {
        localPlayers[event.player.uniqueId] = TilapiaCore.instance.getInternal().createLocalPlayer(event.player)
    }
    @Subscribe("playerLeaveInit")
    fun onPlayerLeave(event: PlayerQuitEvent) {
        localPlayers.remove(event.player.uniqueId)
    }
    @Subscribe("playerDeathMessageRemoval")
    fun onPlayerDeath(event: PlayerDeathEvent) {
        event.deathMessage = null
    }

    fun getAsLocalPlayer(player: Player): LocalNetworkPlayer {
        return localPlayers[player.uniqueId]!!
    }
    operator fun get(uuid: UUID): NetworkPlayer? {
        return localPlayers[uuid]?:players[uuid]
    }

    fun Player.getLocalPlayer(): LocalNetworkPlayer {
        return getAsLocalPlayer(this)
    }



}