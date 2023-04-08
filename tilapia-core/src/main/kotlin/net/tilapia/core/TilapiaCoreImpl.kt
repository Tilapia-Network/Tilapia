package net.tilapia.core

import net.tilapia.api.TilapiaCore
import net.tilapia.api.commands.CommandsManager
import net.tilapia.api.events.EventsManager
import net.tilapia.api.events.annotation.registerAnnotationBasedListener
import net.tilapia.api.game.Game
import net.tilapia.api.game.GamesManager
import net.tilapia.api.game.GameType
import net.tilapia.api.internal.TilapiaInternal
import net.tilapia.api.player.PlayersManager
import net.tilapia.api.server.TilapiaServer
import net.tilapia.core.commands.CommandTest
import net.tilapia.core.server.LocalServerImpl
import java.util.*
import kotlin.collections.ArrayList

// TODO: Test implementation
class TilapiaCoreImpl: TilapiaCore {

    init {
        // Initialize managers
        PlayersManager
        CommandsManager

        CommandsManager.registerCommand(CommandTest())
    }
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
        if (game in games) {
            throw IllegalArgumentException("Game has already been registered!")
        }
        games.add(game)
        GamesManager.registerManagedGame(game)
        EventsManager.registerAnnotationBasedListener(game)
    }
    override fun removeGame(game: Game) {
        if (!game.managed) {
            throw IllegalArgumentException("Game is not managed!")
        }
        if (game !in games) {
            throw IllegalArgumentException("Game is not registered!")
        }
        games.remove(game)
        GamesManager.unregisterManagedGame(game.gameId)
        EventsManager.registerAnnotationBasedListener(game)
    }

    private val internal = TilapiaInternalImpl(this)
    override fun getInternal(): TilapiaInternal {
        return internal
    }

}