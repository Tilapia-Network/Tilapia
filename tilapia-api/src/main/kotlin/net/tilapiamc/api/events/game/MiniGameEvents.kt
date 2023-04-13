package net.tilapiamc.api.events.game

import net.tilapiamc.api.game.minigame.ManagedMiniGame
import net.tilapiamc.api.player.LocalNetworkPlayer
import org.bukkit.event.HandlerList

class PlayerJoinMiniGameEvent(override val game: ManagedMiniGame, val player: LocalNetworkPlayer): GameEvent(game) {
    companion object {
        val handlerList = HandlerList()
    }

    override fun getHandlers() = handlerList
}
class PlayerQuitMiniGameEvent(override val game: ManagedMiniGame, val player: LocalNetworkPlayer): GameEvent(game) {
    companion object {
        val handlerList = HandlerList()
    }

    override fun getHandlers() = handlerList
}
class SpectatorJoinEvent(override val game: ManagedMiniGame, val player: LocalNetworkPlayer): GameEvent(game) {
    companion object {
        val handlerList = HandlerList()
    }

    override fun getHandlers() = handlerList
}
class SpectatorQuitEvent(override val game: ManagedMiniGame, val player: LocalNetworkPlayer): GameEvent(game) {
    companion object {
        val handlerList = HandlerList()
    }

    override fun getHandlers() = handlerList
}