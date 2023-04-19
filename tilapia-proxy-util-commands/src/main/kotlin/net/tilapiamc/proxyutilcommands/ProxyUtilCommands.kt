package net.tilapiamc.proxyutilcommands

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.Dependency
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.proxy.ProxyServer
import net.tilapiamc.proxyapi.TilapiaProxyAPI
import net.tilapiamc.proxyutilcommands.commands.CommandFind
import net.tilapiamc.proxyutilcommands.commands.CommandSend
import net.tilapiamc.proxyutilcommands.commands.CommandSpectate
import org.slf4j.Logger
import javax.inject.Inject


@Plugin(id = "proxy-util-commandsa", name = "Proxy Util Commands", version = "1.0", dependencies = [Dependency(id = "tilapia-proxy-core")])
class ProxyUtilCommands @Inject constructor(val proxy: ProxyServer, val logger: Logger) {

    @Subscribe
    fun onInitialize(event: ProxyInitializeEvent) {
        TilapiaProxyAPI.instance.commandManager.registerCommand(CommandFind())
        TilapiaProxyAPI.instance.commandManager.registerCommand(CommandSend())
        TilapiaProxyAPI.instance.commandManager.registerCommand(CommandSpectate())
    }

}