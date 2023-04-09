package net.tilapiamc.core

import net.tilapiamc.api.TilapiaCore
import net.tilapiamc.api.commands.CommandsManager
import net.tilapia.api.events.EventsManager
import net.tilapia.api.events.annotation.Subscribe
import net.tilapia.api.events.annotation.registerAnnotationBasedListener
import net.tilapia.api.events.annotation.unregisterAnnotationBasedListener
import net.tilapia.api.game.Game
import net.tilapia.api.game.GamesManager
import net.tilapia.api.game.GameType
import net.tilapia.api.game.ManagedGame
import net.tilapia.api.internal.TilapiaInternal
import net.tilapia.api.player.PlayersManager
import net.tilapia.api.server.TilapiaServer
import net.tilapiamc.core.commands.CommandTest
import net.tilapiamc.core.server.LocalServerImpl
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import java.util.*
import kotlin.collections.ArrayList

// TODO: Test implementation
class TilapiaCoreImpl: net.tilapiamc.api.TilapiaCore {

    init {
        // Initialize managers
        PlayersManager
        net.tilapiamc.api.commands.CommandsManager
        EventsManager.registerAnnotationBasedListener(this)

        net.tilapiamc.api.commands.CommandsManager.registerCommand(CommandTest())
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
        Bukkit.broadcastMessage("Removing game ${game.gameId} / ${game.players.size}")
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