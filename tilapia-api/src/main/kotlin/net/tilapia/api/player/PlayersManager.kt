package net.tilapia.api.player

import net.tilapia.api.TilapiaCore
import net.tilapia.api.events.annotation.Listener
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import java.util.UUID

object PlayersManager {

    val localPlayers = HashMap<UUID, LocalNetworkPlayer>()
    val players = HashMap<UUID, NetworkPlayer>()

    @Listener("playerJoinInit")
    fun onPlayerJoin(event: PlayerJoinEvent) {
        localPlayers[event.player.uniqueId] = TilapiaCore.instance.getInternal().createLocalPlayer(event.player)
    }
    @Listener("playerLeaveInit")
    fun onPlayerLeave(event: PlayerJoinEvent) {
        localPlayers.remove(event.player.uniqueId)
    }

    fun getAsLocalPlayer(player: Player): LocalNetworkPlayer {
        return localPlayers[player.uniqueId]!!
    }
    operator fun get(uuid: UUID): NetworkPlayer? {
        return localPlayers[uuid]?:players[uuid]
    }



}