package net.tilapiamc.proxyapi

import com.velocitypowered.api.proxy.Player
import net.tilapiamc.proxyapi.game.Game
import net.tilapiamc.proxyapi.player.LocalNetworkPlayer
import net.tilapiamc.proxyapi.player.NetworkPlayer
import net.tilapiamc.proxyapi.servers.LocalTilapiaServer
import net.tilapiamc.proxyapi.servers.TilapiaServer

interface TilapiaProxyInternal {

    fun sendToGame(player: NetworkPlayer, game: Game, forceJoin: Boolean, spectate: Boolean)
    fun createLocalPlayer(bukkitPlayer: Player): LocalNetworkPlayer
    fun createLocalServer(tilapiaServer: TilapiaServer): LocalTilapiaServer

}