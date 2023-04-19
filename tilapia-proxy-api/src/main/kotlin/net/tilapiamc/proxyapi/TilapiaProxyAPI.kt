package net.tilapiamc.proxyapi

import com.velocitypowered.api.proxy.ProxyServer
import net.tilapiamc.common.language.LanguageManager
import net.tilapiamc.proxyapi.command.ProxyCommandsManager
import net.tilapiamc.proxyapi.events.EventsManager
import net.tilapiamc.proxyapi.player.PlayersManager
import net.tilapiamc.proxyapi.servers.LocalServerManager
import org.jetbrains.exposed.sql.Database

interface TilapiaProxyAPI {


    companion object {
        lateinit var instance: TilapiaProxyAPI
    }

    val proxy: ProxyServer
    val eventsManager: EventsManager
    val commandManager: ProxyCommandsManager
    val playersManager: PlayersManager
    val languageManager: LanguageManager
    val gameFinder: GameFinder
    val internal: TilapiaProxyInternal
    val localServerManager: LocalServerManager

    fun getDatabase(databaseName: String): Database

}