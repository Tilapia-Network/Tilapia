package net.tilapia.api.game.lobby

import net.tilapia.api.TilapiaCore
import net.tilapia.api.game.GameType
import net.tilapia.api.game.ManagedGame
import org.bukkit.World

abstract class ManagedLobby(
    val core: TilapiaCore,
    override val gameWorld: World

): Lobby(core.getLocalServer(), core.provideGameId(GameType.LOBBY), true), ManagedGame {

    override fun end() {
        core.removeGame(this)
    }



}