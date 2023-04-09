package net.tilapiamc.api.events.game

import net.tilapiamc.api.game.Game
import net.tilapiamc.api.game.ManagedGame
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerQuitEvent

abstract class GameEvent(val game: ManagedGame): Event() {

}