package net.tilapiamc.gameextension.rules.impl.minigame

import net.tilapiamc.api.events.annotation.Subscribe
import net.tilapiamc.api.events.game.PlayerJoinGameEvent
import net.tilapiamc.gameextension.rules.impl.RuleNoDestruction
import net.tilapiamc.gameextension.rules.impl.RuleNoTimeChange
import net.tilapiamc.spigotcommon.game.minigame.LocalMiniGame
import net.tilapiamc.spigotcommon.game.minigame.stage.MiniGameStage
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.event.player.PlayerJoinEvent

class StageWaiting(miniGame: LocalMiniGame): MiniGameStage(miniGame, "Waiting") {

    override fun onStart() {
        addRule(RuleNoTimeChange(miniGame))
        addRule(RuleNoDestruction(miniGame))

        Bukkit.broadcastMessage("Stage waiting started")
    }

    override fun onEnd() {
    }

    @Subscribe("waiting-onPlayerJoin")
    fun onPlayerJoin(event: PlayerJoinGameEvent) {
        event.player.sendMessage("${ChatColor.GREEN}Waiting...")
    }

}