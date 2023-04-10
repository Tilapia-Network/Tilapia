package net.tilapiamc.fleetwars

import net.tilapiamc.api.TilapiaCore
import net.tilapiamc.api.game.ManagedGame
import net.tilapiamc.api.player.LocalNetworkPlayer
import net.tilapiamc.api.player.NetworkPlayer
import net.tilapiamc.gameextension.rules.impl.minigame.StageWaiting
import net.tilapiamc.spigotcommon.game.minigame.LocalMiniGame
import net.tilapiamc.spigotcommon.game.minigame.stage.MiniGameStage
import org.bukkit.World

class FleetWars(core: TilapiaCore, gameWorld: World): LocalMiniGame(core, gameWorld, "fleetwars", "fleetwars") {
    override val defaultStage: MiniGameStage = StageWaiting(this)
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