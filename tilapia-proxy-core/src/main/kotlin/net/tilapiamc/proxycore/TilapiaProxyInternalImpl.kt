package net.tilapiamc.proxycore

import com.velocitypowered.api.proxy.Player
import kotlinx.coroutines.runBlocking
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.tilapiamc.common.language.LanguageKeyDelegation
import net.tilapiamc.communication.PlayerInfo
import net.tilapiamc.proxyapi.PlayerJoinResult
import net.tilapiamc.proxyapi.TilapiaProxyInternal
import net.tilapiamc.proxyapi.game.Game
import net.tilapiamc.proxyapi.player.LocalNetworkPlayer
import net.tilapiamc.proxyapi.player.NetworkPlayer
import net.tilapiamc.proxyapi.servers.LocalTilapiaServer
import net.tilapiamc.proxyapi.servers.TilapiaServer
import java.util.*

class TilapiaProxyInternalImpl(val proxyApi: TilapiaProxyCore): TilapiaProxyInternal {
    val SEND_TO_A_GAME by LanguageKeyDelegation("$&8正在傳送你至 &7%1\$s$&8...")


    override fun sendToGame(player: NetworkPlayer, game: Game, forceJoin: Boolean, spectate: Boolean): PlayerJoinResult {
        if (player is LocalNetworkPlayer) {
            player.logger.debug("Sending player to ${game.gameId}")
            player.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(player.getLanguageBundle()[SEND_TO_A_GAME].format(game.shortGameId)))
        }

        return runBlocking {
            val result = proxyApi.communication.send(player.uuid, game.gameId, forceJoin, spectate).get()
            PlayerJoinResult(result.success, result.chance, result.message)
        }

    }



    override fun createLocalPlayer(bukkitPlayer: Player): LocalNetworkPlayer {
        val player = LocalPlayerImpl(proxyApi, bukkitPlayer)
        runBlocking {
            proxyApi.session.login(PlayerInfo(player.playerName, player.uniqueId, Locale.TRADITIONAL_CHINESE, null))
        }
        return player
    }

    override fun createLocalServer(tilapiaServer: TilapiaServer): LocalTilapiaServer {
        return LocalServerImpl(tilapiaServer.address, tilapiaServer.proxyId, tilapiaServer.serverId)
    }
}