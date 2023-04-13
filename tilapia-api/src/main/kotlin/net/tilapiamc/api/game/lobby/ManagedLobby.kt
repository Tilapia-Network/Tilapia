package net.tilapiamc.api.game.lobby

import net.tilapiamc.api.TilapiaCore
import net.tilapiamc.api.game.GameType
import net.tilapiamc.api.game.ManagedGame
import net.tilapiamc.api.player.LocalNetworkPlayer
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bukkit.World
import java.util.*

abstract class ManagedLobby(
    val core: TilapiaCore,
    final override val gameWorld: World,
    lobbyType: String
): Lobby(core.getLocalServer(), core.provideGameId(GameType.LOBBY), true, lobbyType), ManagedGame {
    final override val logger: Logger = LogManager.getLogger("Lobby $gameId")

    val localPlayers: List<LocalNetworkPlayer>
        get() = super.players.filterIsInstance<LocalNetworkPlayer>()
    init {
        logger.info("Assigned world: ${gameWorld.name} to lobby $shortGameId")
    }

    override fun end() {
        onEnd()
        core.removeGame(this)
    }

    override fun getManagedGameId(): UUID {
        return gameId
    }

    override fun start() {
        onStart()
    }
}