package net.tilapiamc.api.player

import net.tilapiamc.api.TilapiaCore
import net.tilapiamc.api.events.EventsManager
import net.tilapiamc.common.events.annotation.Subscribe
import net.tilapiamc.common.events.annotation.registerAnnotationBasedListener
import org.bukkit.entity.Player
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.spigotmc.event.player.PlayerSpawnLocationEvent
import java.util.*

object PlayersManager {
    init {
        EventsManager.registerAnnotationBasedListener(this)
    }

    val localPlayers = HashMap<UUID, LocalNetworkPlayer>()
    val players = HashMap<UUID, NetworkPlayer>()

    @Subscribe("playerJoinInit")
    fun onPlayerJoin(event: PlayerSpawnLocationEvent) {
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