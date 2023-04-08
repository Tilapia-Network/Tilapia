package net.tilapia.api.game.minigame

import net.tilapia.api.TilapiaCore
import net.tilapia.api.game.GameType
import net.tilapia.api.game.ManagedGame
import org.bukkit.World

abstract class ManagedMiniGame(
    val core: TilapiaCore,
    override val gameWorld: World
): MiniGame(core.getLocalServer(), core.provideGameId(GameType.MINIGAME), true), ManagedGame {


    override fun end() {
        core.removeGame(this)
    }

}