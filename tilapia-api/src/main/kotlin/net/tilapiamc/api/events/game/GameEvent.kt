package net.tilapiamc.api.events.game

import net.tilapiamc.api.game.ManagedGame
import org.bukkit.event.Event

abstract class GameEvent(open val game: ManagedGame): Event() {

}