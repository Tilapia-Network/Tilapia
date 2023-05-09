package net.tilapiamc.dummycore

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
import net.tilapiamc.api.game.ManagedGame
import net.tilapiamc.api.internal.TilapiaInternal
import net.tilapiamc.api.networking.GameFinder
import net.tilapiamc.api.player.PlayersManager
import net.tilapiamc.api.server.TilapiaServer
import net.tilapiamc.common.events.annotation.registerAnnotationBasedListener
import net.tilapiamc.common.events.annotation.unregisterAnnotationBasedListener
import net.tilapiamc.common.language.LanguageManager
import net.tilapiamc.database.blockingDbQuery
import net.tilapiamc.dummycore.commands.CommandJoinLocal
import net.tilapiamc.dummycore.commands.CommandLobbyLocal
import net.tilapiamc.dummycore.commands.CommandSpectateLocal
import net.tilapiamc.dummycore.language.LanguageManagerImpl
import net.tilapiamc.dummycore.main.Main
import net.tilapiamc.dummycore.networking.GameFinderImpl
import net.tilapiamc.dummycore.server.LocalServerImpl
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
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList


class TilapiaCoreImpl : TilapiaCore {
    val schemaAccess = ArrayList<String>()
    val logger = LogManager.getLogger("TilapiaCore")

    init {

    }

    init {
        // Initialize managers
        PlayersManager
        SpigotCommandsManager

        DummyCoreConfig.reload()
        EventsManager.registerAnnotationBasedListener(this)
        EventsManager.listenForEvent(ServerTickEvent::class.java)
        EventsManager.listenForEvent(EntityTickEvent::class.java)
    }
    private val localServer = LocalServerImpl(UUID.randomUUID(), UUID.randomUUID())
    val games = ArrayList<ManagedGame>()
    override val languageManager: LanguageManager = LanguageManagerImpl
    override val localGameManager: GamesManager = GamesManager()
    override val gameFinder: GameFinder = GameFinderImpl(this)   // TODO: Communication
    override lateinit var adventure: BukkitAudiences
    override var shuttingDown: Boolean = false
    fun onEnable() {
        adventure = BukkitAudiences.create(getPlugin())

        val schemas = ArrayList<String>()
        blockingDbQuery(Database.connect(DummyCoreConfig.databaseUrl, user = DummyCoreConfig.databaseUsername, password = DummyCoreConfig.databasePassword)) {
            for (plugin in Bukkit.getPluginManager().plugins) {
                if (plugin is TilapiaPlugin) {
                    schemas.addAll(plugin.schemaAccess)
                    for (schemaAccess1 in plugin.schemaAccess) {
                        SchemaUtils.createDatabase(schemaAccess1)
                    }
                }
            }
            SchemaUtils.createDatabase("logs")
        }
        schemaAccess.clear()
        schemaAccess.addAll(schemas)

        LanguageCore
        LanguageCommand
        LanguageGame

        SpigotCommandsManager.registerCommand(CommandJoinLocal())
        SpigotCommandsManager.registerCommand(CommandLobbyLocal())
        SpigotCommandsManager.registerCommand(CommandSpectateLocal())


        Bukkit.getScheduler().runTaskLater(getPlugin(), {
            val map = Class.forName("java.lang.ApplicationShutdownHooks").getDeclaredField("hooks").also {
                it.isAccessible = true
            }
                .get(null) as IdentityHashMap<Thread, Thread>
            map.clear()
            Runtime.getRuntime().addShutdownHook(Thread {
                var doneLock = Object()
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
            })
        }, 1)
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

    override fun updateGame(game: ManagedGame) {
        // Lol we don't need this
    }
    override fun requireSchemaAccess(schema: String) {
    }

    override fun getDatabase(databaseName: String): Database {
        if (databaseName !in schemaAccess) {
            throw IllegalArgumentException("Schema access not registered")
        }
        return Database.connect(DummyCoreConfig.databaseUrl + "/$databaseName", user = DummyCoreConfig.databaseUsername, password = DummyCoreConfig.databasePassword)
    }

    override val ranksManager: RanksManager by lazy {
        RanksManager(getDatabase(RanksManager.DATABASE_NAME))
    }

    fun onDisable() {
        for (allGame in localGameManager.getAllLocalGames()) {
            if (allGame.managed && allGame is ManagedGame) {
                allGame.end()
            }
        }

        try {
            // Upload lop
            println("Uploading logs")
            val logBytes = File("logs/latest.log").readBytes()
            val database = getDatabase("logs")
            blockingDbQuery(database) {
                SchemaUtils.createMissingTablesAndColumns(TableLogs)
                TableLogs.insert {
                    it[this.content] = ExposedBlob(logBytes)
                    it[this.logTime] = LocalDateTime.now()
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }

    }

}