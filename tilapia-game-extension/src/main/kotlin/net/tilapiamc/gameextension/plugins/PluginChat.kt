package net.tilapiamc.gameextension.plugins

import net.tilapiamc.api.player.LocalNetworkPlayer
import net.tilapiamc.api.player.PlayersManager.getLocalPlayer
import net.tilapiamc.common.events.annotation.Subscribe
import net.tilapiamc.spigotcommon.game.plugin.GamePlugin
import org.bukkit.event.player.AsyncPlayerChatEvent

class PluginChat(val chatMessageFormat: (player: LocalNetworkPlayer, message: String) -> String): GamePlugin() {
    override fun onEnable() {
        eventManager.registerListener(this)
    }

    override fun onDisable() {

    }

    @Subscribe("chat-onChat")
    fun onChat(event: AsyncPlayerChatEvent) {
        val chatMessage = chatMessageFormat(event.player.getLocalPlayer(), event.message)
        if (chatMessage.isEmpty()) {
            return
        }
        for (player in game.players.filterIsInstance<LocalNetworkPlayer>()) {
            player.sendMessage(chatMessage)
        }
    }

}