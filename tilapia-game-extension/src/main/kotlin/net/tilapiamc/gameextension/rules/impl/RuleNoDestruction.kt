package net.tilapiamc.gameextension.rules.impl

import net.tilapia.api.events.annotation.Subscribe
import net.tilapiamc.spigotcommon.game.AbstractRule
import net.tilapiamc.spigotcommon.game.LocalGame
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityInteractEvent

class RuleNoDestruction(
    game: LocalGame,
    val protectEntities: Boolean = true,
    val protectUseEntities: Boolean = true,
    val protectPlayers: Boolean = true,
    val protectUsePlayers: Boolean = true,
    val protectBlockPlacement: Boolean = true,
    val protectBlockUse: Boolean = true,
    val protectItemUse: Boolean = true,
    val protectBlockBreak: Boolean = true
): AbstractRule("NoDestruction", game) {

    @Subscribe("NoDestruction-onEntityDamage")
    fun onEntityDamage(event: EntityDamageEvent) {
        if (event is Player) {
            if (protectPlayers) {
                event.isCancelled = true
            }
            return
        }
        if (protectEntities) {
            event.isCancelled = true
            return
        }
    }

    @Subscribe("NoDestruction-onEntityUse")
    fun onEntityUse(event: EntityInteractEvent) {
        if (event is Player) {
            if (protectPlayers) {
                event.isCancelled = true
            }
            return
        }
        if (protectEntities) {
            event.isCancelled = true
            return
        }
    }

}