package net.tilapiamc.proxyapi

import com.velocitypowered.api.proxy.ProxyServer
import net.tilapiamc.common.language.LanguageManager
import net.tilapiamc.proxyapi.command.ProxyCommandManager
import net.tilapiamc.proxyapi.events.EventsManager

interface TilapiaProxyAPI {

    val proxy: ProxyServer
    val eventsManager: EventsManager
    val commandManager: ProxyCommandManager
    val languageManager: LanguageManager
    val gameFinder: GameFinder
    val internal: TilapiaProxyInternal

}