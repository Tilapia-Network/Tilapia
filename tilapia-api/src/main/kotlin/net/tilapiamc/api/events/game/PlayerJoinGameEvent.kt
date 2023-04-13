package net.tilapiamc.api.events.game

import net.tilapiamc.api.game.Game
import net.tilapiamc.api.game.ManagedGame
import net.tilapiamc.api.player.LocalNetworkPlayer
import org.bukkit.event.Cancellable
import org.bukkit.event.HandlerList

open class PlayerJoinGameEvent(game: ManagedGame, val player: LocalNetworkPlayer): GameEvent(game) {



    companion object {
        val handlerList = HandlerList()
    }

    override fun getHandlers(): HandlerList {
        return handlerList
    }



}