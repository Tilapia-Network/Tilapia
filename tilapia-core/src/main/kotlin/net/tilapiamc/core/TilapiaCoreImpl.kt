package net.tilapiamc.core

import io.ktor.websocket.*
import kotlinx.coroutines.runBlocking
import me.fan87.plugindevkit.events.EntityTickEvent
import me.fan87.plugindevkit.events.ServerTickEvent
import net.tilapiamc.api.TilapiaCore
import net.tilapiamc.api.TilapiaPlugin
import net.tilapiamc.api.commands.LanguageCommand
import net.tilapiamc.api.commands.SpigotCommandsManager
import net.tilapiamc.api.events.EventsManager
import net.tilapiamc.api.game.GameType
import net.tilapiamc.api.game.GamesManager
import net.tilapiamc.api.game.IGame
import net.tilapiamc.api.game.ManagedGame
import net.tilapiamc.api.game.lobby.Lobby
import net.tilapiamc.api.game.minigame.MiniGame
import net.tilapiamc.api.internal.TilapiaInternal
import net.tilapiamc.api.networking.GameFinder
import net.tilapiamc.api.player.NetworkPlayer
import net.tilapiamc.api.player.PlayersManager
import net.tilapiamc.api.server.TilapiaServer
import net.tilapiamc.common.SuspendEventTarget
import net.tilapiamc.common.events.annotation.registerAnnotationBasedListener
import net.tilapiamc.common.events.annotation.unregisterAnnotationBasedListener
import net.tilapiamc.common.language.LanguageManager
import net.tilapiamc.communication.*
import net.tilapiamc.communication.api.ServerCommunication
import net.tilapiamc.communication.api.ServerCommunicationSession
import net.tilapiamc.core.accepting.PlayerAccepter
import net.tilapiamc.core.commands.CommandJoinLocal
import net.tilapiamc.core.commands.CommandLobbyLocal
import net.tilapiamc.core.commands.CommandSpectateLocal
import net.tilapiamc.core.language.LanguageManagerImpl
import net.tilapiamc.core.main.Main
import net.tilapiamc.core.networking.GameFinderImpl
import net.tilapiamc.core.networking.NetworkPlayerImpl
import net.tilapiamc.core.networking.NetworkServerImpl
import net.tilapiamc.core.server.LocalServerImpl
import net.tilapiamc.language.LanguageCore
import net.tilapiamc.language.LanguageGame
import org.apache.logging.log4j.LogManager
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

const val DISCONNECT_REASON = "Plugin disabled"

class TilapiaCoreImpl : TilapiaCore {

    val communication = ServerCommunication("testKey", "http://localhost:8080")
    lateinit var session: ServerCommunicationSession
    lateinit var sessionThread: Thread
    private lateinit var localServer: LocalServerImpl
    val logger = LogManager.getLogger("TilapiaCore")


    init {

    }

    fun onEnable() {
        val schemas = ArrayList<String>()
        for (plugin in Bukkit.getPluginManager().plugins) {
            if (plugin is TilapiaPlugin) {
                schemas.addAll(plugin.schemaAccess)
            }
        }
        // Initialize managers
        PlayersManager
        SpigotCommandsManager



        EventsManager.registerAnnotationBasedListener(this)
        EventsManager.listenForEvent(ServerTickEvent::class.java)
        EventsManager.listenForEvent(EntityTickEvent::class.java)
        val lock = Object()
        var initialized = false
        sessionThread = Thread {
            runBlocking {
                communication.start(schemas, { SuspendEventTarget(it) }, { player, gameId, forceJoin ->
                    val game = localGameManager.getLocalGameById(gameId) ?: run {
                        runBlocking {
                            try {
                                communication.endGame(gameId)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        return@start JoinResult(false, 0.0, "The game is not found")
                    }
                    val result = game.couldAdd(NetworkPlayerImpl(session, player), forceJoin)
                    JoinResult(result.type.success, result.chance, result.message)
                }, Bukkit.getPort()) {
                    session = this
                    onServerConnected.add {
                        initialized = true
                        synchronized(lock) {
                            lock.notifyAll()
                        }
                    }
                    onSessionClosed.add {
                        if (it.closeReason?.message != DISCONNECT_REASON) {
                            logger.error("Connection to central server is closed! Shutting down... ${it.closeReason}")
                            Bukkit.getServer().shutdown()
                        }
                    }
                    onPlayerAccepted.add {
                        accepter.handleAcceptPlayerPacket(this@TilapiaCoreImpl, it)
                    }
                }
            }
        }
        sessionThread.start()
        synchronized(lock) {
            lock.wait(5000)
        }
        if (!initialized) {
            logger.error("Failed to connect to central server! Shutting down...")
            Bukkit.getServer().shutdown()
        }
        localServer = LocalServerImpl(session.proxyId, session.serverId)
        NetworkServerImpl.cache[localServer.serverId] = localServer

    }

    val games = ArrayList<ManagedGame>()
    override val languageManager: LanguageManager = LanguageManagerImpl
    override val localGameManager: GamesManager = GamesManager()
    override val gameFinder: GameFinder = GameFinderImpl(this)   // TODO: Communication
    val accepter = PlayerAccepter().also { EventsManager.registerAnnotationBasedListener(it) }


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
        runBlocking {
            communication.registerGame(game.toGameData())
        }
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
        }
        games.remove(game)
        localGameManager.unregisterManagedGame(game.gameId)
        EventsManager.unregisterAnnotationBasedListener(game)
        runBlocking {
            session.communication.endGame(game.gameId)
        }
    }

    private val internal = TilapiaInternalImpl(this)
    override fun getInternal(): TilapiaInternal {
        return internal
    }

    override fun getPlugin(): JavaPlugin {
        return JavaPlugin.getPlugin(Main::class.java)
    }

    val schemaAccess = ArrayList<String>()
    override fun requireSchemaAccess(schema: String) {
        if (schema !in schemaAccess) {
            logger.info("Registered schema access: $schema")
            schemaAccess.add(schema)
        }
    }

    fun onDisable() {
        for (allGame in localGameManager.getAllLocalGames()) {
            if (allGame.managed && allGame is ManagedGame) {
                allGame.end()
            }
        }
        runBlocking {
            session.closeSession(CloseReason(CloseReason.Codes.NORMAL, DISCONNECT_REASON))
        }
    }

    companion object {
        fun IGame.toGameData(): GameData {
            if (this is Lobby) {
                return GameData(null, this.toLobbyInfo())
            }
            if (this is MiniGame) {
                return GameData(this.toMiniGameInfo(), null)
            }
            throw IllegalArgumentException("The game is not lobby or minigame")
        }

        fun Lobby.toLobbyInfo(): LobbyInfo {
            return LobbyInfo(server.serverId, gameId, lobbyType, players.map { it.toPlayerInfo() })
        }

        fun MiniGame.toMiniGameInfo(): MiniGameInfo {
            return MiniGameInfo(server.serverId, gameId, lobbyType, players.map { it.toPlayerInfo() }, miniGameType)
        }

        fun NetworkPlayer.toPlayerInfo(): PlayerInfo {
            // TODO: Inefficiency (calling where() two times)
            return PlayerInfo(playerName, uuid, locale, where()!!.gameId)
        }
    }

}