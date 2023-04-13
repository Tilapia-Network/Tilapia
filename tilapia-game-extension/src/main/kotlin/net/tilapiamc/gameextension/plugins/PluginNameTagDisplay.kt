package net.tilapiamc.gameextension.plugins

import me.fan87.plugindevkit.events.EntityTickEvent
import net.tilapiamc.api.events.annotation.Subscribe
import net.tilapiamc.api.events.game.PlayerJoinGameEvent
import net.tilapiamc.api.events.game.PlayerQuitGameEvent
import net.tilapiamc.api.player.LocalNetworkPlayer
import net.tilapiamc.api.player.PlayersManager.getLocalPlayer
import net.tilapiamc.api.utils.StringUtils
import net.tilapiamc.spigotcommon.game.plugin.GamePlugin
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Team
import org.bukkit.util.StringUtil
import java.util.UUID

class PluginNameTagDisplay(val getPrefix: (LocalNetworkPlayer) -> String, val getSuffix: (LocalNetworkPlayer) -> String): GamePlugin() {


    override fun onEnable() {
        for (localNetworkPlayer in game.players.filterIsInstance<LocalNetworkPlayer>()) {
            addPlayer(localNetworkPlayer)
        }
        eventManager.registerListener(this)
    }

    override fun onDisable() {
        for (localNetworkPlayer in game.players.filterIsInstance<LocalNetworkPlayer>()) {
            removePlayer(localNetworkPlayer)
        }
    }

    @Subscribe("nameTagDisplay-onEntityTick")
    fun onTick(event: EntityTickEvent) {
        val player = event.entity
        if (player !is Player) return
        try {
            val localPlayer = player.getLocalPlayer()
            val prefix = getPrefix(localPlayer)
            val suffix = getSuffix(localPlayer)
            val team = playerTeams[localPlayer.uniqueId]!!
            team.prefix = prefix
            team.suffix = suffix
        } catch (e: NullPointerException) {}
    }

    val playerTeams = HashMap<UUID, Team>()

    @Subscribe("nameTagDisplay-onPlayerJoin")
    fun onPlayerJoin(event: PlayerJoinGameEvent) {
        addPlayer(event.player)
    }
    @Subscribe("nameTagDisplay-onPlayerQuit")
    fun onPlayerQuit(event: PlayerQuitGameEvent) {
        removePlayer(event.player)
    }

    fun addPlayer(player: Player) {
        val team = player.scoreboard.registerNewTeam(StringUtils.getRandomString(16))
        playerTeams[player.uniqueId] = team
        team.addEntry(player.name)
    }
    
    fun removePlayer(player: Player) {
        playerTeams[player.uniqueId]?.unregister()
        
    }
}