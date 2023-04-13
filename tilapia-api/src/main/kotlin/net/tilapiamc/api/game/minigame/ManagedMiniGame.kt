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
    final override val gameWorld: World,
    lobbyType: String,
    miniGameType: String
): MiniGame(core.getLocalServer(), core.provideGameId(GameType.MINIGAME), true, lobbyType, miniGameType), ManagedGame {


    final override val logger: Logger = LogManager.getLogger("MiniGame $gameId")
    val inGamePlayers = ArrayList<LocalNetworkPlayer>()
    val spectatorPlayers = ArrayList<LocalNetworkPlayer>()

    init {
        logger.info("Assigned world: ${gameWorld.name} to mini game $shortGameId")
    }

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