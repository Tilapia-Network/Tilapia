package net.tilapiamc.gameextension.rules.impl.minigame

import me.fan87.plugindevkit.events.ServerTickEvent
import net.tilapiamc.api.events.annotation.Subscribe
import net.tilapiamc.api.events.game.PlayerJoinGameEvent
import net.tilapiamc.api.events.game.PlayerQuitGameEvent
import net.tilapiamc.gameextension.rules.impl.RuleNoDestruction
import net.tilapiamc.gameextension.rules.impl.RuleNoTimeChange
import net.tilapiamc.spigotcommon.game.minigame.LocalMiniGame
import net.tilapiamc.spigotcommon.game.minigame.stage.MiniGameStage
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class StageWaiting(miniGame: LocalMiniGame, val countDownTime: Int): MiniGameStage(miniGame, "Waiting") {

    var time = 0

    override fun onStart() {
        addRule(RuleNoTimeChange(miniGame))
        addRule(RuleNoDestruction(miniGame))
    }

    override fun onEnd() {
    }

    @Subscribe("waiting-onTick")
    fun onTick(event: ServerTickEvent) {
        time -= 1
        updateTime()
    }

    @Subscribe("waiting-onPlayerJoin")
    fun onPlayerJoin(event: PlayerJoinGameEvent) {
        event.player.sendMessage("${ChatColor.GREEN}Waiting...")
    }
    @Subscribe("waiting-onPlayerQuit")
    fun onPlayerQuit(event: PlayerQuitGameEvent) {
        event.player.sendMessage("${ChatColor.GREEN}Waiting...")
    }

    fun updatePlayers() {

    }

    fun updateTime() {

    }

}