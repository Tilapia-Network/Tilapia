package net.tilapiamc.gameextension.rules

import net.tilapiamc.common.events.annotation.Subscribe
import net.tilapiamc.spigotcommon.game.AbstractRule
import net.tilapiamc.spigotcommon.game.LocalGame
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.*
import org.bukkit.event.inventory.InventoryInteractEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerPickupItemEvent
import org.bukkit.event.vehicle.VehicleDamageEvent

class RuleNoDestruction(
    game: LocalGame,
    val protectVehicles: Boolean = true,
    val protectEntities: Boolean = true,
    val protectUseEntities: Boolean = true,
    val protectPlayers: Boolean = true,
    val protectPlayersHunger: Boolean = true,
    val protectUsePlayers: Boolean = true,
    val protectBlockPlacement: Boolean = true,
    val protectBlockUse: Boolean = true,
    val protectItemUse: Boolean = true,
    val protectBlockBreak: Boolean = true,
    val protectEntityDestruction: Boolean = true,
    val protectItemPickUp: Boolean = true,
    val protectItemDrop: Boolean = true,
    val protectPlayerPhysical: Boolean = true,
    val protectPlayerInventoryChange: Boolean = true,
    val ignorePlayer: Player.() -> Boolean = { gameMode == GameMode.CREATIVE }
): AbstractRule("NoDestruction", game) {

    companion object {
        fun spectatorRule(game: LocalGame, ignorePlayer: Player.() -> Boolean): RuleNoDestruction {
            return RuleNoDestruction(
                game,
                protectEntityDestruction = false,
                protectPlayers = true,
                protectEntities = true,
                protectVehicles = true,
                protectPlayersHunger = true,
                protectUsePlayers = true,
                protectUseEntities = true,
                protectBlockBreak = true,
                protectBlockPlacement = true,
                protectItemUse = true,
                protectPlayerPhysical = true,
                protectBlockUse = true,
                protectItemDrop = true,
                protectItemPickUp = true,
                protectPlayerInventoryChange = true,
                ignorePlayer = ignorePlayer
            )
        }
    }

    @Subscribe("NoDestruction-onEntityDestruction")
    fun onEntityDestruction(event: EntityInteractEvent) {
        if (protectEntityDestruction) {
            event.isCancelled = true
        }
    }

    @Subscribe("NoDestruction-onEntityDestruction")
    fun onEntityExplode(event: EntityExplodeEvent) {
        if (protectEntityDestruction) {
            event.isCancelled = true
        }
    }
    @Subscribe("NoDestruction-onEntityDestruction")
    fun onBlockExplode(event: BlockExplodeEvent) {
        if (protectEntityDestruction) {
            event.isCancelled = true
        }
    }

    @Subscribe("NoDestruction-onEntityDamage")
    fun onEntityDamage(event: EntityDamageEvent) {
        if (event is EntityDamageByEntityEvent) {
            if (event.damager is Player && ignorePlayer(event.damager as Player)) {
                return
            }
        }
        if (event.entity is Player) {
            if (ignorePlayer(event.entity as Player)) {
                return
            }
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
    @Subscribe("NoDestruction-onVehicleDamage")
    fun onVehicleDamage(event: VehicleDamageEvent) {
        if (event.attacker is Player && ignorePlayer(event.attacker as Player)) {
            return
        }
        if (protectVehicles) {
            event.isCancelled = true
            return
        }
    }

    @Subscribe("NoDestruction-onHungerChange")
    fun onHungerChange(event: FoodLevelChangeEvent) {
        if (protectPlayersHunger) {
            if (ignorePlayer(event.entity as Player)) {
                return
            }
            event.isCancelled = true
            val player = event.entity
            if (player is Player) {
                player.foodLevel = 20
                player.saturation = 1f
            }
        }
    }

    @Subscribe("NoDestruction-onEntityUse")
    fun onEntityUse(event: PlayerInteractEntityEvent) {
        if (ignorePlayer(event.player)) {
            return
        }
        if (event.rightClicked is Player) {
            if (protectUsePlayers) {
                event.isCancelled = true
            }
            return
        }
        if (protectUseEntities) {
            event.isCancelled = true
            return
        }
    }
    @Subscribe("NoDestruction-onBlockDestroy")
    fun onBlockDestroy(event: BlockBreakEvent) {
        if (ignorePlayer(event.player)) {
            return
        }
        if (protectBlockBreak) {
            event.isCancelled = true
            return
        }
    }
    @Subscribe("NoDestruction-onBlockPlace")
    fun onBlockPlace(event: BlockPlaceEvent) {
        if (ignorePlayer(event.player)) {
            return
        }
        if (protectBlockPlacement) {
            event.isCancelled = true
            return
        }
    }
    @Subscribe("NoDestruction-onItemUse")
    fun onItemUse(event: PlayerInteractEvent) {
        if (ignorePlayer(event.player)) {
            return
        }
        if (event.hasItem()) {
            if (protectItemUse) {
                event.isCancelled = true
                return
            }
        }
    }
    @Subscribe("NoDestruction-onPlayerPhysical")
    fun onPlayerPhysical(event: PlayerInteractEvent) {
        if (ignorePlayer(event.player)) {
            return
        }
        if (event.action == Action.PHYSICAL) {
            if (protectPlayerPhysical) {
                event.isCancelled = true
                return
            }
        }
    }
    @Subscribe("NoDestruction-onBlockUse")
    fun onBlockUse(event: PlayerInteractEvent) {
        if (ignorePlayer(event.player)) {
            return
        }
        if (event.hasBlock()) {
            if (protectBlockUse) {
                event.isCancelled = true
                return
            }
        }
    }
    @Subscribe("NoDestruction-onItemDrop")
    fun onItemDrop(event: PlayerDropItemEvent) {
        if (ignorePlayer(event.player)) {
            return
        }
        if (protectItemDrop) {
            event.isCancelled = true
            return
        }
    }
    @Subscribe("NoDestruction-onItemPickup")
    fun onItemPickup(event: PlayerPickupItemEvent) {
        if (ignorePlayer(event.player)) {
            return
        }
        if (protectItemPickUp) {
            event.isCancelled = true
            return
        }
    }
    @Subscribe("NoDestruction-onPlayerInventoryChange")
    fun onPlayerInventoryChange(event: InventoryInteractEvent) {
        if (ignorePlayer(event.whoClicked as Player)) {
            return
        }
        if (event.whoClicked == null) {
            return
        }
        if (protectPlayerInventoryChange) {
            event.isCancelled = true
            return
        }
    }

}