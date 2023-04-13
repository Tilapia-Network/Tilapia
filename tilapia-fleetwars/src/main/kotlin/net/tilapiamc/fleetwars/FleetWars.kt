package net.tilapiamc.fleetwars

import net.tilapiamc.api.TilapiaCore
import net.tilapiamc.api.game.ManagedGame
import net.tilapiamc.api.game.getGameLanguageKey
import net.tilapiamc.api.player.LocalNetworkPlayer
import net.tilapiamc.api.player.NetworkPlayer
import net.tilapiamc.fleetwars.stages.StageInGame
import net.tilapiamc.gameextension.minigame.StageWaiting
import net.tilapiamc.gameextension.rules.RuleNoDestruction
import net.tilapiamc.spigotcommon.game.minigame.LocalMiniGame
import net.tilapiamc.spigotcommon.game.minigame.stage.MiniGameStage
import net.tilapiamc.spigotcommon.utils.TemporaryWorldProvider
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.World

class FleetWars(core: TilapiaCore, gameWorld: World): LocalMiniGame(core, TemporaryWorldProvider.createTemporaryWorldFromWorld(gameWorld), "fleetwars", "fleetwars") {
    override val defaultStage: MiniGameStage = StageWaiting(this, { if (it > 0) 20 * 10 else -1}, 8) {
        currentStage = StageInGame(this)
    }



    override fun onStart() {
        addRule(RuleNoDestruction.spectatorRule(this) {
            isInGame() || gameMode == GameMode.CREATIVE
        })
    }

    override fun onEnd() {

    }

    val gameAlreadyStarted = getGameLanguageKey("GAME_ALREADY_STARTED", "遊戲已經開始")

    override fun couldAddPlayer(networkPlayer: NetworkPlayer, forceJoin: Boolean): ManagedGame.PlayerJoinResult {
        if (defaultStage is StageWaiting) {
            return ManagedGame.PlayerJoinResult(ManagedGame.PlayerJoinResultType.ACCEPTED, 1.0)
        }
        return ManagedGame.PlayerJoinResult(ManagedGame.PlayerJoinResultType.DENIED, 0.0, networkPlayer.getLanguageBundle()[gameAlreadyStarted])
    }

    override fun addPlayer(networkPlayer: LocalNetworkPlayer) {
    }

    override fun removePlayer(networkPlayer: LocalNetworkPlayer) {

    }
}