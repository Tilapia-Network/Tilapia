package net.tilapiamc.core

import me.fan87.plugindevkit.events.EntityTickEvent
import me.fan87.plugindevkit.events.ServerTickEvent
import net.tilapiamc.api.TilapiaCore
import net.tilapiamc.api.commands.LanguageCommand
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
import net.tilapiamc.api.networking.GameFinder
import net.tilapiamc.api.player.PlayersManager
import net.tilapiamc.api.server.TilapiaServer
import net.tilapiamc.communication.api.ServerCommunication
import net.tilapiamc.communication.api.ServerCommunicationSession
import net.tilapiamc.core.commands.CommandJoinLocal
import net.tilapiamc.core.commands.CommandLobbyLocal
import net.tilapiamc.core.commands.CommandSpectateLocal
import net.tilapiamc.core.language.LanguageManagerImpl
import net.tilapiamc.core.main.Main
import net.tilapiamc.core.networking.LocalGameFinderImpl
import net.tilapiamc.core.networking.NetworkServerImpl
import net.tilapiamc.core.server.LocalServerImpl
import net.tilapiamc.language.LanguageCore
import net.tilapiamc.language.LanguageGame
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import kotlin.collections.ArrayList

// TODO: Test implementation
class TilapiaCoreImpl: TilapiaCore {

    val communication = ServerCommunication("testKey")
    val session: ServerCommunicationSession
    private val localServer: LocalServerImpl

    init {
        // Initialize managers
        PlayersManager
        SpigotCommandsManager



        EventsManager.registerAnnotationBasedListener(this)
        EventsManager.listenForEvent(ServerTickEvent::class.java)
        EventsManager.listenForEvent(EntityTickEvent::class.java)

        session = communication.start()
        localServer = LocalServerImpl(session.proxyId, session.serverId)

    }
    init {
        NetworkServerImpl.cache[localServer.serverId] = localServer
    }
    val games = ArrayList<ManagedGame>()
    override val languageManager: LanguageManager = LanguageManagerImpl
    override val localGameManager: GamesManager = GamesManager()
    override val gameFinder: GameFinder = LocalGameFinderImpl(this)   // TODO: Communication

    fun registerCommands() {

        LanguageCore
        LanguageCommand
        LanguageGame

        SpigotCommandsManager.registerCommand(CommandJoinLocal())
        SpigotCommandsManager.registerCommand(CommandLobbyLocal())
        SpigotCommandsManager.registerCommand(CommandSpectateLocal())
    }

    override fun provideGameId(gameType: GameType): UUID {
        return UUID.randomUUID()
    }

    override fun getLocalServer(): TilapiaServer {
        return localServer
    }

    override fun addGame(game: ManagedGame) {
        if (!game.managed) {
            throw IllegalArgumentException("Game is not managed!")
        }
        if (game in games) {
            throw IllegalArgumentException("Game has already been registered!")
        }
        if (localGameManager.getAllLocalGames().any { it is ManagedGame && it.gameWorld.name == game.gameWorld.name }) {
            throw IllegalArgumentException("The world is already assigned to another game")
        }
        game.start()
        games.add(game)
        localGameManager.registerManagedGame(game)
        EventsManager.registerAnnotationBasedListener(game)
    }
    override fun removeGame(game: ManagedGame) {
        if (!game.managed) {
            throw IllegalArgumentException("Game is not managed!")
        }
        if (game !in games) {
            throw IllegalArgumentException("Game is not registered!")
        }
        for (player in ArrayList(game.players)) {
            val localPlayer = PlayersManager.localPlayers[player.uuid]
            localPlayer!!.kickPlayer(localPlayer.getLanguageBundle()[LanguageCore.TEMP_GAME_STOPPED])
//            TODO("Add to fall back server logic")
        }
        games.remove(game)
        localGameManager.unregisterManagedGame(game.gameId)
        EventsManager.unregisterAnnotationBasedListener(game)
    }

    private val internal = TilapiaInternalImpl(this)
    override fun getInternal(): TilapiaInternal {
        return internal
    }

    override fun getPlugin(): JavaPlugin {
        return JavaPlugin.getPlugin(Main::class.java)
    }

    fun onDisable() {
        for (allGame in localGameManager.getAllLocalGames()) {
            if (allGame.managed && allGame is ManagedGame) {
                allGame.end()
            }
        }
    }


}