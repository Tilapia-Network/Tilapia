package net.tilapiamc.api.game

import net.tilapiamc.api.player.NetworkPlayer
import net.tilapiamc.api.server.TilapiaServer
import java.util.*

abstract class Game(
    override val server: TilapiaServer,
    override val gameType: GameType,
    override val gameId: UUID,
    override val managed: Boolean,
): IGame {

    override val players = ArrayList<NetworkPlayer>()

    override fun hashCode(): Int {
        return gameId.hashCode()
    }
}