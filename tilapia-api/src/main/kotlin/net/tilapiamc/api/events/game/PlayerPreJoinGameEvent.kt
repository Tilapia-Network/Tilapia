package net.tilapiamc.api.events.game

import net.tilapiamc.api.game.Game
import net.tilapiamc.api.game.IGame
import net.tilapiamc.api.game.ManagedGame
import net.tilapiamc.api.player.LocalNetworkPlayer
import net.tilapiamc.api.player.NetworkPlayer
import org.bukkit.event.Cancellable
import org.bukkit.event.HandlerList

class PlayerPreJoinGameEvent(game: ManagedGame, val player: NetworkPlayer, var result: ManagedGame.PlayerJoinResult): GameEvent(game) {



    companion object {
        val handlerList = HandlerList()
    }

    override fun getHandlers(): HandlerList {
        return handlerList
    }



}