package net.tilapiamc.api.events.game

import net.tilapiamc.api.game.ManagedGame
import net.tilapiamc.api.player.LocalNetworkPlayer
import org.bukkit.event.HandlerList

open class PlayerQuitGameEvent(game: ManagedGame, val player: LocalNetworkPlayer): GameEvent(game) {

    companion object {
        val handlerList = HandlerList()
    }

    override fun getHandlers(): HandlerList {
        return handlerList
    }

}