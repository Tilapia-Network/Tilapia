package net.tilapiamc.fleetwars.stages

import net.tilapiamc.fleetwars.FleetWars
import net.tilapiamc.gameextension.minigame.StageWaiting
import net.tilapiamc.spigotcommon.game.minigame.stage.MiniGameStage

class StageInGame(override val miniGame: FleetWars): MiniGameStage(miniGame, "FleetWarsInGame") {
    override fun onStart() {
        for (player in miniGame.localPlayers) {
            player.sendMessage("Game started!")
        }
    }

    override fun onEnd() {

    }
}