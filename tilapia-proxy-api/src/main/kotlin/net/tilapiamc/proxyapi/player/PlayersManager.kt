package net.tilapiamc.proxyapi.player

import com.velocitypowered.api.event.connection.DisconnectEvent
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent
import com.velocitypowered.api.proxy.Player
import net.tilapiamc.common.events.annotation.Subscribe
import net.tilapiamc.common.events.annotation.registerAnnotationBasedListener
import net.tilapiamc.proxyapi.TilapiaProxyAPI
import java.util.*

class PlayersManager(val proxyApi: TilapiaProxyAPI) {

    init {
        proxyApi.eventsManager.registerAnnotationBasedListener(this)
    }

    val localPlayers = HashMap<UUID, LocalNetworkPlayer>()
    val players = HashMap<UUID, NetworkPlayer>()

    @Subscribe("playerJoinInit")
    fun onPlayerJoin(event: PlayerChooseInitialServerEvent) {
        localPlayers[event.player.uniqueId] = proxyApi.internal.createLocalPlayer(event.player)
    }
    @Subscribe("playerLeaveInit")
    fun onPlayerLeave(event: DisconnectEvent) {
        localPlayers.remove(event.player.uniqueId)
    }

    fun getAsLocalPlayer(player: Player): LocalNetworkPlayer {
        return localPlayers[player.uniqueId]!!
    }
    operator fun get(uuid: UUID): NetworkPlayer? {
        return localPlayers[uuid]?:players[uuid]
    }

    companion object {
        fun Player.getLocalPlayer(): LocalNetworkPlayer {
            return TilapiaProxyAPI.instance.playersManager.getAsLocalPlayer(this)
        }
    }

}