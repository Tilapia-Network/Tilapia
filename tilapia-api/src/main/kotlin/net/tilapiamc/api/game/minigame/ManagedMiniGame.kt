package net.tilapiamc.api.game.minigame

import net.tilapiamc.api.TilapiaCore
import net.tilapiamc.api.game.GameType
import net.tilapiamc.api.game.ManagedGame
import net.tilapiamc.api.player.LocalNetworkPlayer
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bukkit.World
import java.util.*

abstract class ManagedMiniGame(
    val core: TilapiaCore,
    override val gameWorld: World,
    lobbyType: String,
    miniGameType: String
): MiniGame(core.getLocalServer(), core.provideGameId(GameType.MINIGAME), true, lobbyType, miniGameType), ManagedGame {
    override val logger: Logger = LogManager.getLogger("Game $gameId")

    override fun getManagedGameId(): UUID {
        return gameId
    }


    override fun start() {
        onStart()
    }

    override fun end() {
        onEnd()
        core.removeGame(this)
    }

    fun addSpectator(networkPlayer: LocalNetworkPlayer) {
        this.spectatorPlayers.add(networkPlayer)
        super.add(networkPlayer)
    }
    override fun add(networkPlayer: LocalNetworkPlayer) {
        this.inGamePlayers.add(networkPlayer)
        super.add(networkPlayer)
    }

    override fun remove(networkPlayer: LocalNetworkPlayer) {
        this.inGamePlayers.remove(networkPlayer)
        this.spectatorPlayers.remove(networkPlayer)
        super.remove(networkPlayer)
    }
}