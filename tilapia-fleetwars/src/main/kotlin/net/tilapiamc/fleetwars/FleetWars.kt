package net.tilapiamc.fleetwars

import net.tilapiamc.api.TilapiaCore
import net.tilapiamc.api.game.ManagedGame
import net.tilapiamc.api.player.LocalNetworkPlayer
import net.tilapiamc.api.player.NetworkPlayer
import net.tilapiamc.fleetwars.stages.StageInGame
import net.tilapiamc.gameextension.minigame.StageWaiting
import net.tilapiamc.spigotcommon.game.minigame.LocalMiniGame
import net.tilapiamc.spigotcommon.game.minigame.stage.MiniGameStage
import org.bukkit.Bukkit
import org.bukkit.World

class FleetWars(core: TilapiaCore, gameWorld: World): LocalMiniGame(core, gameWorld, "fleetwars", "fleetwars") {
    override val defaultStage: MiniGameStage = StageWaiting(this, { if (it > 0) 20 * 10 else -1}, 8) {
        currentStage = StageInGame(this)
    }



    override fun onStart() {

    }

    override fun onEnd() {

    }

    override fun couldAddPlayer(networkPlayer: NetworkPlayer): Double {
        return 1.0
    }

    override fun preAddPlayer(networkPlayer: NetworkPlayer): ManagedGame.PlayerJoinResult {
        return ManagedGame.PlayerJoinResult(ManagedGame.PlayerJoinResultType.ACCEPTED)
    }

    override fun addPlayer(networkPlayer: LocalNetworkPlayer) {

    }

    override fun removePlayer(networkPlayer: LocalNetworkPlayer) {

    }
}