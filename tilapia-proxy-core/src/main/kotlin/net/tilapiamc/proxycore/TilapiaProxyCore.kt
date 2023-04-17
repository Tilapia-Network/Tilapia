package net.tilapiamc.proxycore

import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.proxy.ProxyServer
import net.tilapiamc.proxyapi.TilapiaProxyAPI
import net.tilapiamc.proxyapi.command.ProxyCommandManager
import net.tilapiamc.proxyapi.events.EventsManager
import org.slf4j.Logger
import javax.inject.Inject

@Plugin(id = "tilapia-proxy-core",
    name = "Tilapia Proxy Core",
    version = "1.0.0")
class TilapiaProxyCore @Inject constructor(val proxy: ProxyServer, val logger: Logger): TilapiaProxyAPI {
    override val commandManager: ProxyCommandManager = ProxyCommandManager()
    override lateinit var eventsManager: EventsManager

    init {
    }

    @com.velocitypowered.api.event.Subscribe
    fun onInitialize(event: ProxyInitializeEvent) {
        println("Plugin Initialize, I guess?")
        eventsManager = EventsManager(this, proxy)
    }

}