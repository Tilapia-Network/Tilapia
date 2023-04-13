package net.tilapiamc.api.events.game

import net.tilapiamc.api.game.ManagedGame
import net.tilapiamc.api.player.NetworkPlayer
import org.bukkit.event.HandlerList

class PlayerCheckAddEvent(game: ManagedGame, val player: NetworkPlayer, var result: ManagedGame.PlayerJoinResult): GameEvent(game) {



    companion object {
        val handlerList = HandlerList()
    }

    override fun getHandlers(): HandlerList {
        return handlerList
    }



}