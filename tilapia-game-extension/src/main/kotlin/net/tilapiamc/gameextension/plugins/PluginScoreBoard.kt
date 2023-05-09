package net.tilapiamc.gameextension.plugins

import fr.mrmicky.fastboard.FastBoard
import me.fan87.plugindevkit.events.EntityTickEvent
import net.tilapiamc.api.events.game.PlayerJoinGameEvent
import net.tilapiamc.api.events.game.PlayerQuitGameEvent
import net.tilapiamc.api.player.LocalNetworkPlayer
import net.tilapiamc.api.player.PlayersManager.getLocalPlayer
import net.tilapiamc.common.events.annotation.Subscribe
import net.tilapiamc.spigotcommon.game.plugin.GamePlugin
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.util.UUID

class PluginScoreBoard(
    val titleProvider: (LocalNetworkPlayer) -> String,
    val contentProvider: (LocalNetworkPlayer) -> String,
    val refreshRate: Int = 3
): GamePlugin() {

    val boards = HashMap<UUID, FastBoard>()

    override fun onEnable() {
        eventManager.registerListener(this)
    }

    override fun onDisable() {

    }

    @Subscribe("scoreboard-onPlayerTick")
    fun onPlayerTick(event: EntityTickEvent) {
        val player = event.entity
        if (player !is Player) {
            return
        }
        if (player.ticksLived % refreshRate == 0) {
            val board = player.getBoard()
            val localPlayer = player.getLocalPlayer()
            board.updateTitle(titleProvider(localPlayer)) // Dw, the FastBoard api does lazy check
            val map = contentProvider(localPlayer).split("\n").map { if (it.length > 30) it.substring(0, 30) else it }
            val out = ArrayList(map)
            out.add("")
            out.add("${ChatColor.DARK_GRAY}除錯資訊: ${ChatColor.GRAY}${game.shortGameId}")
            out.add("${ChatColor.YELLOW}play.tilapiamc.net")

            board.updateLines(out)
        }
    }
    @Subscribe("scoreboard-onPlayerJoin")
    fun onPlayerJoin(event: PlayerJoinGameEvent) {
        val fastBoard = FastBoard(event.player.bukkitPlayer)
        boards[event.player.uniqueId] = fastBoard
    }
    @Subscribe("scoreboard-onPlayerQuit")
    fun onPlayerQuit(event: PlayerQuitGameEvent) {
        event.player.getBoard().delete()
        boards.remove(event.player.uniqueId)
    }

    private fun Player.getBoard(): FastBoard = boards[uniqueId]!!

}