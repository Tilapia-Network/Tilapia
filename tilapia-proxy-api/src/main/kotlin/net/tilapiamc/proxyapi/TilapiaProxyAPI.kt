package net.tilapiamc.proxyapi

import com.velocitypowered.api.proxy.ProxyServer
import net.tilapiamc.common.language.LanguageManager
import net.tilapiamc.proxyapi.command.ProxyCommandManager
import net.tilapiamc.proxyapi.events.EventsManager
import net.tilapiamc.proxyapi.player.PlayersManager
import net.tilapiamc.proxyapi.servers.LocalServerManager

interface TilapiaProxyAPI {


    companion object {
        lateinit var instance: TilapiaProxyAPI
    }

    val proxy: ProxyServer
    val eventsManager: EventsManager
    val commandManager: ProxyCommandManager
    val playersManager: PlayersManager
    val languageManager: LanguageManager
    val gameFinder: GameFinder
    val internal: TilapiaProxyInternal
    val localServerManager: LocalServerManager

}