package net.tilapiamc.api.game.minigame

import net.tilapiamc.api.TilapiaCore
import net.tilapiamc.api.game.GameType
import net.tilapiamc.api.game.ManagedGame
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bukkit.World
import java.util.*

abstract class ManagedMiniGame(
    val core: TilapiaCore,
    override val gameWorld: World
): MiniGame(core.getLocalServer(), core.provideGameId(GameType.MINIGAME), true), ManagedGame {
    override val logger: Logger = LogManager.getLogger("Game $gameId")

    override fun getManagedGameId(): UUID {
        return gameId
    }

    override fun end() {
        core.removeGame(this)
    }

}