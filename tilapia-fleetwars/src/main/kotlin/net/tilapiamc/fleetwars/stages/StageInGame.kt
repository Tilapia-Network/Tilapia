package net.tilapiamc.fleetwars.stages

import net.tilapiamc.fleetwars.FleetWars
import net.tilapiamc.gameextension.minigame.StageWaiting
import net.tilapiamc.gameextension.plugins.PluginNameTagDisplay
import net.tilapiamc.language.LanguageGame
import net.tilapiamc.spigotcommon.game.minigame.stage.MiniGameStage
import org.bukkit.ChatColor

class StageInGame(override val miniGame: FleetWars): MiniGameStage(miniGame, "FleetWarsInGame") {

    override fun onStart() {
        applyPlugin(PluginNameTagDisplay(getPrefix = {
             if (it.isInGame()) {
                 return@PluginNameTagDisplay "${ChatColor.RED}${ChatColor.BOLD}R ${ChatColor.RED}"
             } else {
                 return@PluginNameTagDisplay it.getLanguageBundle()[LanguageGame.SPECTATOR_PREFIX]
             }
        }, getSuffix = { "" }))

        for (player in miniGame.localPlayers) {
            player.sendMessage("Game started!")
        }
    }

    override fun onEnd() {

    }
}