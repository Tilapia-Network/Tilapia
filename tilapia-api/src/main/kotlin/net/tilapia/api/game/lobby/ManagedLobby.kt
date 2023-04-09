package net.tilapia.api.game.lobby

import net.tilapia.api.TilapiaCore
import net.tilapia.api.game.GameType
import net.tilapia.api.game.ManagedGame
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bukkit.World
import java.util.*

abstract class ManagedLobby(
    val core: TilapiaCore,
    override val gameWorld: World

): Lobby(core.getLocalServer(), core.provideGameId(GameType.LOBBY), true), ManagedGame {
    override val logger: Logger = LogManager.getLogger("Lobby $gameId")

    override fun end() {
        core.removeGame(this)
    }

    override fun getManagedGameId(): UUID {
        return gameId
    }
}