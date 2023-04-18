package net.tilapiamc.core.accepting

import kotlinx.coroutines.runBlocking
import me.fan87.plugindevkit.PluginInstanceGrabber
import net.tilapiamc.api.game.ManagedGame
import net.tilapiamc.api.game.minigame.ManagedMiniGame
import net.tilapiamc.api.game.minigame.MiniGame
import net.tilapiamc.api.player.LocalNetworkPlayer
import net.tilapiamc.api.player.PlayersManager.getLocalPlayer
import net.tilapiamc.common.events.annotation.Subscribe
import net.tilapiamc.communication.session.server.server.SPacketServerAcceptPlayer
import net.tilapiamc.core.TilapiaCoreImpl
import org.apache.logging.log4j.LogManager
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.spigotmc.event.player.PlayerSpawnLocationEvent
import java.util.*

class PlayerAccepter {
    class AcceptedInfo(val game: ManagedGame, val spectate: Boolean)
    val acceptedPlayers = HashMap<UUID, AcceptedInfo?>()
    val logger = LogManager.getLogger("PlayerAccepter")

    fun handleAcceptPlayerPacket(core: TilapiaCoreImpl, packet: SPacketServerAcceptPlayer) {
        Bukkit.getScheduler().runTask(PluginInstanceGrabber.getPluginInstance()) {
            Bukkit.getPlayer(packet.player)?.kickPlayer("STATUS_SEND_TO_" + packet.serverId)
        }
        val game = core.localGameManager.getLocalGameById(packet.gameId)?: run {
            runBlocking {
                logger.warn("Player is being accepted to a game that's not found")
                try {
                    core.communication.endGame(packet.gameId)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            acceptedPlayers[packet.player] = null
            return
        }
        acceptedPlayers[packet.player] = AcceptedInfo(game, if (game is MiniGame) packet.spectate else false)
    }

    @Subscribe("acceptedPlayerJoin", mustRunAfter = ["playerJoinInit"])
    fun handlePlayerJoin(event: PlayerSpawnLocationEvent) {
        val acceptedInfo = acceptedPlayers[event.player.uniqueId]
        acceptedPlayers.remove(event.player.uniqueId)
        if (acceptedInfo == null) {
            // We are not expecting to receive player data as the player may be joining with direct IP
            event.player.kickPlayer("${ChatColor.RED}The server has sent you to a wrong game. Please rejoin\n" +
                    "${ChatColor.RED}伺服器傳送你到了一個錯誤的遊戲！請重新加入")
            return
        }

        val player = event.player.getLocalPlayer()
        val spectate = acceptedInfo.spectate
        val game = acceptedInfo.game
        if (game.managed && game is ManagedGame && player.isLocal && player is LocalNetworkPlayer) {
            player.resetPlayerState()
            event.spawnLocation = game.getSpawnLocation(player)
            if (spectate) {
                (game as ManagedMiniGame).addSpectator(player)
            } else {
                game.add(player)
            }
        }
        player.currentGameId = game.gameId
    }


}