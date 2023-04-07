package net.tilapia.api.player

import net.tilapia.api.game.Game
import net.tilapia.api.server.TilapiaServer
import java.util.UUID

abstract class NetworkPlayer {

    abstract val name: String
    abstract val uuid: UUID
    abstract val currentServer: TilapiaServer
    abstract val currentGame: Game

}