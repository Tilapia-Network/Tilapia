package net.tilapiamc.api.game

import net.tilapia.api.player.NetworkPlayer
import net.tilapia.api.server.TilapiaServer
import java.util.UUID

abstract class Game(
    override val server: TilapiaServer,
    override val gameType: GameType,
    override val gameId: UUID,
    override val managed: Boolean
): IGame {

    override val players = ArrayList<NetworkPlayer>()

}