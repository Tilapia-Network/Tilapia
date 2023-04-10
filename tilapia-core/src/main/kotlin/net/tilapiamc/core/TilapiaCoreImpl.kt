package net.tilapiamc.core

import me.fan87.plugindevkit.events.EntityTickEvent
import me.fan87.plugindevkit.events.ServerTickEvent
import net.tilapiamc.api.TilapiaCore
import net.tilapiamc.api.commands.SpigotCommandsManager
import net.tilapiamc.api.events.EventsManager
import net.tilapiamc.api.events.annotation.registerAnnotationBasedListener
import net.tilapiamc.api.events.annotation.unregisterAnnotationBasedListener
import net.tilapiamc.api.game.Game
import net.tilapiamc.api.game.GamesManager
import net.tilapiamc.api.game.GameType
import net.tilapiamc.api.game.ManagedGame
import net.tilapiamc.api.internal.TilapiaInternal
import net.tilapiamc.api.language.LanguageManager
import net.tilapiamc.api.player.PlayersManager
import net.tilapiamc.api.server.TilapiaServer
import net.tilapiamc.core.commands.CommandJoinLocal
import net.tilapiamc.core.commands.CommandLobbyLocal
import net.tilapiamc.core.commands.CommandTest
import net.tilapiamc.core.language.LanguageManagerImpl
import net.tilapiamc.core.server.LocalServerImpl
import net.tilapiamc.language.LanguageCore
import org.bukkit.ChatColor
import java.util.*
import kotlin.collections.ArrayList

// TODO: Test implementation
class TilapiaCoreImpl: TilapiaCore {

    init {
        // Initialize managers
        PlayersManager
        SpigotCommandsManager
        EventsManager.registerAnnotationBasedListener(this)
        EventsManager.listenForEvent(ServerTickEvent::class.java)
        EventsManager.listenForEvent(EntityTickEvent::class.java)

        SpigotCommandsManager.registerCommand(CommandTest())
        SpigotCommandsManager.registerCommand(CommandJoinLocal())
        SpigotCommandsManager.registerCommand(CommandLobbyLocal())
    }
    private val localServer = LocalServerImpl()
    val games = ArrayList<Game>()
    override val languageManager: LanguageManager = LanguageManagerImpl()
    override val gamesManager: GamesManager = GamesManager()


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
        if (game is ManagedGame) {
            game.start()
        }
        games.add(game)
        gamesManager.registerManagedGame(game)
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
            localPlayer!!.kickPlayer(localPlayer.getLanguageBundle()[LanguageCore.TEMP_GAME_STOPPED])
//            TODO("Add to fall back server logic")
        }
        games.remove(game)
        gamesManager.unregisterManagedGame(game.gameId)
        EventsManager.unregisterAnnotationBasedListener(game)
    }

    private val internal = TilapiaInternalImpl(this)
    override fun getInternal(): TilapiaInternal {
        return internal
    }

    fun onDisable() {
        for (allGame in gamesManager.getAllGames()) {
            if (allGame.managed && allGame is ManagedGame) {
                allGame.end()
            }
        }
    }


}