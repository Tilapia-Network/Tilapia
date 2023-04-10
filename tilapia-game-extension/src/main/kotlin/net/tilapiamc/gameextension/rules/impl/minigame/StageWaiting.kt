package net.tilapiamc.gameextension.rules.impl.minigame

import me.fan87.plugindevkit.events.ServerTickEvent
import net.tilapiamc.api.events.annotation.Subscribe
import net.tilapiamc.api.events.game.PlayerJoinGameEvent
import net.tilapiamc.api.events.game.PlayerQuitGameEvent
import net.tilapiamc.api.player.LocalNetworkPlayer
import net.tilapiamc.gameextension.rules.impl.RuleNoDestruction
import net.tilapiamc.gameextension.rules.impl.RuleNoTimeChange
import net.tilapiamc.language.LanguageGame
import net.tilapiamc.spigotcommon.game.minigame.LocalMiniGame
import net.tilapiamc.spigotcommon.game.minigame.stage.MiniGameStage

class StageWaiting(miniGame: LocalMiniGame,
                   val countDownTime: Int,
                   val minPlayers: Int,
                   val maxPlayers: Int,
): MiniGameStage(miniGame, "Waiting") {

    var time = -1

    override fun onStart() {
        addRule(RuleNoTimeChange(miniGame))
        addRule(RuleNoDestruction(miniGame))
    }

    override fun onEnd() {
    }

    @Subscribe("waiting-onTick")
    fun onTick(event: ServerTickEvent) {
        updateTime()
    }

    @Subscribe("waiting-onPlayerJoin")
    fun onPlayerJoin(event: PlayerJoinGameEvent) {
        updatePlayers(event.player)
        for (player in miniGame.players) {
            val localNetworkPlayer = player as LocalNetworkPlayer
            localNetworkPlayer.sendMessage(localNetworkPlayer.getLanguageBundle()[LanguageGame.WAITING_PLAYER_JOIN].format(
                event.player.name, miniGame.inGamePlayers.size, maxPlayers
            ))
        }
    }
    @Subscribe("waiting-onPlayerQuit")
    fun onPlayerQuit(event: PlayerQuitGameEvent) {
        updatePlayers(event.player)
        for (player in miniGame.players) {
            val localNetworkPlayer = player as LocalNetworkPlayer
            localNetworkPlayer.sendMessage(localNetworkPlayer.getLanguageBundle()[LanguageGame.WAITING_PLAYER_QUIT].format(
                event.player.name, miniGame.inGamePlayers.size, maxPlayers
            ))
        }
    }

    fun updatePlayers(player: LocalNetworkPlayer) {
        if (miniGame.inGamePlayers.size >= minPlayers) {
            if (time <= 0) {
                time = countDownTime
            }
            player.sendMessage(player.getLanguageBundle()[LanguageGame.WAITING_COUNTDOWN_MSG].format(time / 20))
        } else {
            time = -1
            player.sendMessage(player.getLanguageBundle()[LanguageGame.WAITING_WAITING_MSG])
        }
    }

    fun updateTime() {
        time--
        if (time % 20 == 0) {
            for (player in miniGame.inGamePlayers) {
                player.sendMessage(player.getLanguageBundle()[LanguageGame.WAITING_COUNTDOWN].format(time / 20))
            }
        }
        if (time )
    }

}