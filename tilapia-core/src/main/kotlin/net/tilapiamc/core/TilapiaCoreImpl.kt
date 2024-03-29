package net.tilapiamc.core

import com.google.gson.JsonObject
import io.ktor.websocket.*
import kotlinx.coroutines.runBlocking
import me.fan87.plugindevkit.events.EntityTickEvent
import me.fan87.plugindevkit.events.ServerTickEvent
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import net.minecraft.server.v1_8_R3.MinecraftServer
import net.tilapiamc.api.TilapiaCore
import net.tilapiamc.api.TilapiaPlugin
import net.tilapiamc.api.commands.LanguageCommand
import net.tilapiamc.api.commands.SpigotCommandsManager
import net.tilapiamc.api.events.EventsManager
import net.tilapiamc.api.events.server.ServerShutdownEvent
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
import net.tilapiamc.common.docker.DockerUtils
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
import net.tilapiamc.core.tables.TableLogs
import net.tilapiamc.database.blockingDbQuery
import net.tilapiamc.language.LanguageCore
import net.tilapiamc.language.LanguageGame
import net.tilapiamc.ranks.RanksManager
import org.apache.logging.log4j.LogManager
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import java.io.File
import java.net.URI
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

const val DISCONNECT_REASON = "Plugin disabled"

class TilapiaCoreImpl : TilapiaCore {
    val logger = LogManager.getLogger("TilapiaCore")

    val backendAddress: String
    val databaseAddress: String
    override val ranksManager: RanksManager by lazy {
        RanksManager(getDatabase(RanksManager.DATABASE_NAME))
    }
    init {
        if (DockerUtils.isInDocker()) {
            backendAddress = System.getenv("BACKEND_HOST")?:DockerUtils.getContainerGateway()
            logger.info("Docker detected! IP: ${backendAddress}:${System.getenv("MC_PORT")?.toInt()?:DockerUtils.getMinecraftPort()}")
        } else {
            logger.warn("The server should be run in docker! Don't use it for production.")
            backendAddress = System.getenv("BACKEND_HOST")?:"localhost"
        }
        databaseAddress = System.getenv("DATABASE_HOST")?:backendAddress
    }

    val communication = ServerCommunication(System.getenv("API_KEY")?:"testKey", "http://${backendAddress}:8080")
    lateinit var session: ServerCommunicationSession
    lateinit var sessionThread: Thread
    private lateinit var localServer: LocalServerImpl
    override lateinit var adventure: BukkitAudiences
    override var shuttingDown: Boolean = false

    init {
        val map = Class.forName("java.lang.ApplicationShutdownHooks").getDeclaredField("hooks").also {
            it.isAccessible = true
        }
            .get(null) as IdentityHashMap<Thread, Thread>
        val oldMap = HashMap(map)
        map.clear()
        Runtime.getRuntime().addShutdownHook(Thread {
            Thread.sleep(100)
            logger.warn("Server has received SIGTERM, shutting down...")
            shuttingDown = true
            Bukkit.getScheduler().runTask(getPlugin()) {
                EventsManager.fireEvent(ServerShutdownEvent())
            }
            while (localGameManager.getAllLocalGames().isNotEmpty()) {
                for (managedGame in localGameManager.getAllLocalGames().filter { it.canShutdown() }) {
                    Bukkit.getScheduler().runTask(getPlugin()) {
                        managedGame.end()
                    }
                }
                Thread.sleep(1000)
            }
            MinecraftServer.getServer().safeShutdown()
            MinecraftServer.getServer().stop()

            // Upload lop
            val logBytes = File("logs/latest.log").readBytes()
            val database = getDatabase("logs")
            blockingDbQuery(database) {
                SchemaUtils.createMissingTablesAndColumns(TableLogs)
                TableLogs.insert {
                    it[this.content] = ExposedBlob(logBytes)
                    it[this.logTime] = LocalDateTime.now()
                }
            }


            runBlocking {
                session.closeSession(CloseReason(CloseReason.Codes.NORMAL, DISCONNECT_REASON))
            }
        })
    }

    fun onEnable() {
        Bukkit.getScheduler().runTaskLater(getPlugin(), {
            Thread {
                File("SERVER_STARTED").mkdirs()
            }.start()
        }, 1)
        adventure = BukkitAudiences.create(getPlugin())
        val schemas = ArrayList<String>()
        for (plugin in Bukkit.getPluginManager().plugins) {
            if (plugin is TilapiaPlugin) {
                schemas.addAll(plugin.schemaAccess)
            }
        }
        schemaAccess.clear()
        schemaAccess.addAll(schemas)
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
                var dontShutdown = false
                try {
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
                    }, if (DockerUtils.isInDocker()) System.getenv("MC_PORT")?.toInt()?:DockerUtils.getMinecraftPort() else System.getenv("MC_PORT")?.toInt()?:Bukkit.getPort()) {
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
                            dontShutdown = true
                        }
                        onPlayerAccepted.add {
                            accepter.handleAcceptPlayerPacket(this@TilapiaCoreImpl, it)
                        }
                    }
                } finally {
                    if (!dontShutdown) {
                        logger.error("Connection to central server is closed! Shutting down...  UNKNOWN")
                        Bukkit.getServer().shutdown()
                    }
                }

            }
        }
        sessionThread.start()
        logger.info("Connecting...")
        if (!initialized) {
            synchronized(lock) {
                lock.wait(50000)
            }
        }

        if (!initialized) {
            logger.error("Failed to connect to central server! Shutting down...")
            Bukkit.getServer().shutdown()
            return
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
        games.add(game)
        localGameManager.registerManagedGame(game)
        runBlocking {
            communication.registerGame(game.toGameData())
        }
        game.start()
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
        }
        games.remove(game)
        localGameManager.unregisterManagedGame(game.gameId)
        EventsManager.unregisterAnnotationBasedListener(game)
        runBlocking {
            session.communication.endGame(game.gameId)
        }
    }

    override fun updateGame(game: ManagedGame) {
        runBlocking {
            communication.updateGameProperty(game.gameId, game.getProperties())
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

    val databaseCache = HashMap<String, Database>()

    override fun getDatabase(databaseName: String): Database {
        return databaseCache[databaseName]?: run {
            val databaseLogin = session.databaseLogin
            val uri = URI("mysql", null, databaseAddress, 3306, "/$databaseName", null, null)
            val database = Database.connect("jdbc:${uri.toASCIIString()}", user = databaseLogin.username, password = databaseLogin.password)
            databaseCache[databaseName] = database
            database
        }
    }

    fun onDisable() {
        for (allGame in localGameManager.getAllLocalGames()) {
            if (allGame.managed && allGame is ManagedGame) {
                allGame.end()
            }
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
            return LobbyInfo(server.serverId, gameId, lobbyType, players.map { it.toPlayerInfo() }, JsonObject().also { getProperties().entries.forEach { entry -> it.add(entry.key, entry.value) } })
        }

        fun MiniGame.toMiniGameInfo(): MiniGameInfo {
            return MiniGameInfo(server.serverId, gameId, lobbyType, players.map { it.toPlayerInfo() }, miniGameType, spectators.map { it.toPlayerInfo() }, JsonObject().also { getProperties().entries.forEach { entry -> it.add(entry.key, entry.value) } })
        }

        fun NetworkPlayer.toPlayerInfo(): PlayerInfo {
            // TODO: Inefficiency (calling where() two times)
            return PlayerInfo(playerName, uuid, locale, where()!!.gameId)
        }
    }

}