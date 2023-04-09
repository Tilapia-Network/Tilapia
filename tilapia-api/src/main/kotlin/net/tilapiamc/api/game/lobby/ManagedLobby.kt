package net.tilapiamc.api.game.lobby

import net.tilapiamc.api.TilapiaCore
import net.tilapiamc.api.game.GameType
import net.tilapiamc.api.game.ManagedGame
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bukkit.World
import java.util.*

abstract class ManagedLobby(
    val core: net.tilapiamc.api.TilapiaCore,
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