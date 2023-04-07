package net.tilapia.core

import net.tilapia.api.TilapiaCore
import net.tilapia.api.events.EventsManager
import net.tilapia.api.events.annotation.registerAnnotationBasedListener
import net.tilapia.api.game.Game
import net.tilapia.api.game.GameType
import net.tilapia.api.server.TilapiaServer
import net.tilapia.core.server.LocalServerImpl
import java.util.*
import kotlin.collections.ArrayList

class TilapiaCoreImpl: TilapiaCore {
    private val localServer = LocalServerImpl()
    val games = ArrayList<Game>()


    override fun provideGameId(gameType: GameType): UUID {
        return UUID.randomUUID()
    }

    override fun getLocalServer(): TilapiaServer {
        return localServer
    }

    override fun addGame(game: Game) {
        if (!game.managed) {
            throw IllegalArgumentException("Game is not managed!")
        }
        games.add(game)
        EventsManager.registerAnnotationBasedListener(game)
    }

}