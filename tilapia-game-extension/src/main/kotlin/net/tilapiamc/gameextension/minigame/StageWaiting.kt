package net.tilapiamc.gameextension.minigame

import me.fan87.plugindevkit.events.ServerTickEvent
import net.tilapiamc.common.events.annotation.Subscribe
import net.tilapiamc.api.events.game.PlayerJoinGameEvent
import net.tilapiamc.api.events.game.PlayerJoinMiniGameEvent
import net.tilapiamc.api.events.game.PlayerQuitMiniGameEvent
import net.tilapiamc.api.player.LocalNetworkPlayer
import net.tilapiamc.gameextension.plugins.PluginNameTagDisplay
import net.tilapiamc.gameextension.rules.RuleNoDestruction
import net.tilapiamc.gameextension.rules.RuleNoTimeChange
import net.tilapiamc.language.LanguageGame
import net.tilapiamc.spigotcommon.game.minigame.LocalMiniGame
import net.tilapiamc.spigotcommon.game.minigame.stage.MiniGameStage

class StageWaiting(miniGame: LocalMiniGame,
                   val countDownTime: (Int) -> Int,
                   val maxPlayers: Int,
                   val finishCallBack: () -> Unit
): MiniGameStage(miniGame, "Waiting") {

    var time = -1

    override fun onStart() {
        addRule(RuleNoTimeChange(miniGame))
        addRule(RuleNoDestruction(miniGame))
        applyPlugin(PluginNameTagDisplay(getPrefix = { it.prefixColor }, getSuffix = { "" }))
    }

    override fun onEnd() {
    }

    @Subscribe("waiting-onTick")
    fun onTick(event: ServerTickEvent) {
        if (time != -1) {
            updateTime()
        }
    }

    @Subscribe("waiting-onPlayerJoin")
    fun onPlayerJoin(event: PlayerJoinMiniGameEvent) {
        updatePlayers(event.player, true)
        for (player in miniGame.players) {
            val localNetworkPlayer = player as LocalNetworkPlayer
            localNetworkPlayer.sendMessage(localNetworkPlayer.getLanguageBundle()[LanguageGame.WAITING_PLAYER_JOIN].format(
                event.player.name, miniGame.inGamePlayers.size, maxPlayers
            ))
        }
    }
    @Subscribe("waiting-onPlayerQuit")
    fun onPlayerQuit(event: PlayerQuitMiniGameEvent) {
        updatePlayers(event.player, false)
        for (player in miniGame.players) {
            val localNetworkPlayer = player as LocalNetworkPlayer
            localNetworkPlayer.sendMessage(localNetworkPlayer.getLanguageBundle()[LanguageGame.WAITING_PLAYER_QUIT].format(
                event.player.name, miniGame.inGamePlayers.size, maxPlayers
            ))
        }
    }

    @Subscribe("waiting-sendJoinMsg")
    fun sendJoinMsg(event: PlayerJoinGameEvent) {
        val player = event.player
        if (time == -1) {
            player.sendMessage(player.getLanguageBundle()[LanguageGame.WAITING_WAITING_MSG])
        } else {
            player.sendMessage(player.getLanguageBundle()[LanguageGame.WAITING_COUNTDOWN_MSG].format(time / 20))
        }
    }

    fun updatePlayers(player: LocalNetworkPlayer, join: Boolean) {
        val oldTime = time
        val newTime = countDownTime(miniGame.inGamePlayers.size)
        if (newTime != oldTime) {
            if (newTime == -1) {
                time = -1
                for (player1 in miniGame.players.filterIsInstance<LocalNetworkPlayer>()) {
                    player1.sendMessage(player1.getLanguageBundle()[LanguageGame.WAITING_WAITING])
                }
            } else if (newTime < oldTime || oldTime == -1) {
                time = newTime
                for (player1 in miniGame.players.filterIsInstance<LocalNetworkPlayer>()) {
                    player1.sendMessage(player1.getLanguageBundle()[LanguageGame.WAITING_COUNTDOWN].format(time / 20))
                }
            }
        }
        if (join) {
            if (time == -1) {
                player.sendMessage(player.getLanguageBundle()[LanguageGame.WAITING_WAITING_MSG])
            } else {
                player.sendMessage(player.getLanguageBundle()[LanguageGame.WAITING_COUNTDOWN_MSG].format(time / 20))
            }
        }

    }

    fun updateTime() {
        time--
        if (time == 0) {
            countdownFinish()
            return
        }
        if (time % 20 == 0) {
            val sec = time / 20
            if (sec <= 5 || sec % 5 == 0) {
                for (player in miniGame.inGamePlayers) {
                    player.sendMessage(player.getLanguageBundle()[LanguageGame.WAITING_COUNTDOWN].format(time / 20))
                }
            }
        }
    }

    fun countdownFinish() {
        finishCallBack()
    }

}