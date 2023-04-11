package net.tilapiamc.gameextension.rules.impl

import net.tilapiamc.api.events.annotation.Subscribe
import net.tilapiamc.spigotcommon.game.AbstractRule
import net.tilapiamc.spigotcommon.game.LocalGame
import org.bukkit.GameMode
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityChangeBlockEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.entity.EntityInteractEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.entity.ItemSpawnEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.InventoryInteractEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerPickupItemEvent
import org.bukkit.event.vehicle.VehicleDestroyEvent

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
    val ignoreCreative: Boolean = true
): AbstractRule("NoDestruction", game) {

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
            if (ignoreCreative && event.damager is Player && (event.damager as Player).gameMode == GameMode.CREATIVE) {
                return
            }
        }
        if (event.entity is Player) {
            if (ignoreCreative && (event.entity as Player).gameMode == GameMode.CREATIVE) {
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
    @Subscribe("NoDestruction-onVehicleDestroy")
    fun onVehicleDestroy(event: VehicleDestroyEvent) {
        if (protectVehicles) {
            event.isCancelled = true
            return
        }
    }

    @Subscribe("NoDestruction-onHungerChange")
    fun onHungerChange(event: FoodLevelChangeEvent) {
        if (protectPlayersHunger) {
            if (ignoreCreative && event.entity.gameMode == GameMode.CREATIVE) {
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
        if (ignoreCreative && event.player.gameMode == GameMode.CREATIVE) {
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
        if (ignoreCreative && event.player.gameMode == GameMode.CREATIVE) {
            return
        }
        if (protectBlockBreak) {
            event.isCancelled = true
            return
        }
    }
    @Subscribe("NoDestruction-onBlockPlace")
    fun onBlockPlace(event: BlockPlaceEvent) {
        if (ignoreCreative && event.player.gameMode == GameMode.CREATIVE) {
            return
        }
        if (protectBlockPlacement) {
            event.isCancelled = true
            return
        }
    }
    @Subscribe("NoDestruction-onItemUse")
    fun onItemUse(event: PlayerInteractEvent) {
        if (ignoreCreative && event.player.gameMode == GameMode.CREATIVE) {
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
        if (ignoreCreative && event.player.gameMode == GameMode.CREATIVE) {
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
        if (ignoreCreative && event.player.gameMode == GameMode.CREATIVE) {
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
        if (ignoreCreative && event.player.gameMode == GameMode.CREATIVE) {
            return
        }
        if (protectItemDrop) {
            event.isCancelled = true
            return
        }
    }
    @Subscribe("NoDestruction-onItemPickup")
    fun onItemPickup(event: PlayerPickupItemEvent) {
        if (ignoreCreative && event.player.gameMode == GameMode.CREATIVE) {
            return
        }
        if (protectItemPickUp) {
            event.isCancelled = true
            return
        }
    }
    @Subscribe("NoDestruction-onPlayerInventoryChange")
    fun onPlayerInventoryChange(event: InventoryInteractEvent) {
        if (ignoreCreative && event.whoClicked.gameMode == GameMode.CREATIVE) {
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