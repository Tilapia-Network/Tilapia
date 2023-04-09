package net.tilapiamc.core

import net.tilapiamc.api.commands.SpigotCommandsManager
import net.tilapiamc.api.events.EventsManager
import net.tilapiamc.api.events.annotation.registerAnnotationBasedListener
import net.tilapiamc.api.events.annotation.unregisterAnnotationBasedListener
import net.tilapiamc.api.game.Game
import net.tilapiamc.api.game.GamesManager
import net.tilapiamc.api.game.GameType
import net.tilapiamc.api.game.ManagedGame
import net.tilapiamc.api.internal.TilapiaInternal
import net.tilapiamc.api.player.PlayersManager
import net.tilapiamc.api.server.TilapiaServer
import net.tilapiamc.core.commands.CommandTest
import net.tilapiamc.core.server.LocalServerImpl
import org.bukkit.ChatColor
import java.util.*
import kotlin.collections.ArrayList

// TODO: Test implementation
class TilapiaCoreImpl: net.tilapiamc.api.TilapiaCore {

    init {
        // Initialize managers
        PlayersManager
        SpigotCommandsManager
        EventsManager.registerAnnotationBasedListener(this)

        SpigotCommandsManager.registerCommand(CommandTest())
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
        for (player in game.players) {
            val localPlayer = PlayersManager.localPlayers[player.uuid]
            localPlayer!!.kickPlayer("${ChatColor.RED}TODO: Game has been stopped, sending to fall back server")
//            TODO("Add to fall back server logic")
        }
        games.remove(game)
        GamesManager.unregisterManagedGame(game.gameId)
        EventsManager.unregisterAnnotationBasedListener(game)
    }

    private val internal = TilapiaInternalImpl(this)
    override fun getInternal(): TilapiaInternal {
        return internal
    }

    fun onDisable() {
        for (allGame in GamesManager.getAllGames()) {
            if (allGame.managed && allGame is ManagedGame) {
                allGame.end()
            }
        }
    }


}