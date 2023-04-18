package net.tilapiamc.proxycore

import com.velocitypowered.api.event.PostOrder
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import com.velocitypowered.api.event.player.KickedFromServerEvent
import com.velocitypowered.api.event.player.KickedFromServerEvent.DisconnectPlayer
import com.velocitypowered.api.event.player.KickedFromServerEvent.RedirectPlayer
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.proxy.ProxyServer
import io.ktor.websocket.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import net.tilapiamc.common.SuspendEventTarget
import net.tilapiamc.common.language.LanguageKeyDelegation
import net.tilapiamc.common.language.LanguageManager
import net.tilapiamc.communication.api.ProxyCommunication
import net.tilapiamc.communication.api.ProxyCommunicationSession
import net.tilapiamc.proxyapi.GameFinder
import net.tilapiamc.proxyapi.TilapiaProxyAPI
import net.tilapiamc.proxyapi.TilapiaProxyInternal
import net.tilapiamc.proxyapi.TilapiaProxyPlugin
import net.tilapiamc.proxyapi.command.ProxyCommandManager
import net.tilapiamc.proxyapi.events.EventsManager
import net.tilapiamc.proxyapi.player.PlayersManager
import net.tilapiamc.proxyapi.player.PlayersManager.Companion.getLocalPlayer
import net.tilapiamc.proxyapi.servers.LocalServerManager
import net.tilapiamc.proxycore.language.LanguageManagerImpl
import net.tilapiamc.proxycore.networking.GameFinderImpl
import net.tilapiamc.proxycore.networking.NetworkServerImpl
import org.slf4j.Logger
import java.util.*
import javax.inject.Inject
import kotlin.jvm.optionals.getOrNull



@Plugin(id = "tilapia-proxy-core",
    name = "Tilapia Proxy Core",
    version = "1.0.0")
class TilapiaProxyCore @Inject constructor(override val proxy: ProxyServer, val logger: Logger): TilapiaProxyAPI {
    override lateinit var commandManager: ProxyCommandManager
    override lateinit var eventsManager: EventsManager
    override lateinit var playersManager: PlayersManager
    override val languageManager: LanguageManager = LanguageManagerImpl
    override val gameFinder: GameFinder = GameFinderImpl(this)
    override val internal: TilapiaProxyInternal = TilapiaProxyInternalImpl(this)
    override val localServerManager: LocalServerManager = LocalServerManager(this)

    val communication = ProxyCommunication("testKey", "http://localhost:8080")

    init {
        TilapiaProxyAPI.instance = this
    }

    lateinit var session: ProxyCommunicationSession
    lateinit var sessionThread: Thread

    val COULD_NOT_SEND by LanguageKeyDelegation("無法將你傳送至該小遊戲中！請稍後再試")

    @Subscribe
    fun onInitialize(event: ProxyInitializeEvent) {
        eventsManager = EventsManager(this, proxy)
        playersManager = PlayersManager(this)
        commandManager = ProxyCommandManager()

        val schemas = ArrayList<String>()
        for (plugin in proxy.pluginManager.plugins) {
            val pluginInstance = plugin.instance.getOrNull()
            if (pluginInstance is TilapiaProxyPlugin) {
                schemas.addAll(pluginInstance.schemaAccess)
            }
        }


        val lock = Object()
        var initialized = false
        sessionThread = Thread {
            runBlocking {
                communication.start(schemas, { SuspendEventTarget(it) }) {
                    session = this
                    onProxyConnected.add {
                        initialized = true
                        synchronized(lock) {
                            lock.notifyAll()
                        }
                    }
                    onSessionClosed.add {
                        if (it.closeReason?.message == "Proxy shutdown") {
                            return@add
                        }
                        logger.error("Connection to central server is closed! Shutting down... ${it.closeReason}")
                        proxy.shutdown()
                    }
                    onPlayerAccepted.add { packet ->
                        val player = proxy.allPlayers.filter { it.uniqueId == packet.player }.firstOrNull()?:return@add
                        if (player.uniqueId in joinCancel) {
                            joinCancel.remove(player.uniqueId)
                            return@add
                        }
                        if (player.currentServer.getOrNull()?.serverInfo?.name == packet.serverId.toString()) {
                            return@add
                        }
                        if (withContext(Dispatchers.IO) {
                                player.createConnectionRequest(proxy.allServers.first { it.serverInfo.name == packet.serverId.toString() })
                                    .connectWithIndication().get() }) {
                            player.disconnect(Component.text(player.getLocalPlayer().getLanguageBundle()[COULD_NOT_SEND]))
                        }
                    }
                    onServerAdded.add {
                        localServerManager.register(NetworkServerImpl(it.serverInfo))
                    }
                    onServerRemoved.add {
                        localServerManager.servers[it.serverId]?.also { server -> localServerManager.unregister(server) }
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
            proxy.shutdown()
        }
    }

    @Subscribe
    fun onStop(event: ProxyShutdownEvent) {
        runBlocking {
            session.closeSession(CloseReason(CloseReason.Codes.NORMAL, "Proxy shutdown"))
        }
    }

    var joinCancel = arrayListOf<UUID>()

    @Subscribe
    fun onKicked(event: KickedFromServerEvent) {
        if (event.serverKickReason.getOrNull()?.let { it is TextComponent && it.content().startsWith("STATUS_SEND_TO_") } == true) {
            try {
                val serverId =
                    event.serverKickReason.let { (it.get() as TextComponent).content().replace("STATUS_SEND_TO_", "") }
                val server = proxy.allServers.first { it.serverInfo.name == serverId }
                event.result = RedirectPlayer.create(server, Component.empty())
            } catch (e: Exception) {
                e.printStackTrace()
                event.result = DisconnectPlayer.create(Component.text(event.player.getLocalPlayer().getLanguageBundle()[COULD_NOT_SEND]).color(NamedTextColor.RED))
            }
        }
    }

    @Subscribe(order = PostOrder.LATE)
    fun onJoin(event: PlayerChooseInitialServerEvent) {
        val lobby = event.player.getLocalPlayer().findLobbyToJoin("main", false)
        // TODO: Better join behavior
        joinCancel.add(event.player.uniqueId)
        try {
            event.player.getLocalPlayer().send(lobby!!, false, false)
            event.setInitialServer(proxy.allServers.first { it.serverInfo.name == lobby.server.serverId.toString() })
        } finally {
            joinCancel.remove(event.player.uniqueId)
        }
    }


    @Subscribe(order = PostOrder.LATE)
    fun onDisconnect(event: DisconnectEvent) {
        runBlocking {
            session.logout(event.player.uniqueId)
        }
    }

}