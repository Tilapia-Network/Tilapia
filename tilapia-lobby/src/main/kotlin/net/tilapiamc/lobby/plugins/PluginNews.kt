package net.tilapiamc.lobby.plugins

import net.kyori.adventure.inventory.Book
import net.kyori.adventure.text.Component
import net.tilapiamc.api.commands.SpigotCommandsManager
import net.tilapiamc.api.events.game.PlayerJoinGameEvent
import net.tilapiamc.api.player.LocalNetworkPlayer.Companion.getAdventureAudience
import net.tilapiamc.common.events.annotation.Subscribe
import net.tilapiamc.lobby.TilapiaLobbyPlugin
import net.tilapiamc.lobby.commands.CommandNews
import net.tilapiamc.spigotcommon.game.plugin.GamePlugin
import org.bukkit.entity.Player

class PluginNews(val rootPlugin: TilapiaLobbyPlugin): GamePlugin() {

    val newsCommand = CommandNews(this)

    override fun onEnable() {
        eventManager.registerListener(this)
        SpigotCommandsManager.registerCommand(newsCommand)
    }

    override fun onDisable() {
        SpigotCommandsManager.unregisterCommand(newsCommand)
    }

    @Subscribe("news-onPlayerJoin")
    fun onPlayerJoin(event: PlayerJoinGameEvent) {
        show(event.player)
    }

    fun show(player: Player) {
        if (rootPlugin.content.isEmpty()) return
        player.getAdventureAudience().openBook(Book.book(Component.empty(), Component.empty(), rootPlugin.contentToComponent()))
    }

}