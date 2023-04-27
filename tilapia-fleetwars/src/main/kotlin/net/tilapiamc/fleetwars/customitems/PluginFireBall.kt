package net.tilapiamc.fleetwars.customitems

import me.fan87.plugindevkit.events.EntityTickEvent
import net.tilapiamc.common.events.annotation.Subscribe
import net.tilapiamc.fleetwars.config.FleetWarsConfig
import net.tilapiamc.spigotcommon.game.plugin.GamePlugin
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Fireball
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.entity.ExplosionPrimeEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.util.Vector

class PluginFireBall(val sandbox: Boolean): GamePlugin() {
    override fun onEnable() {
        eventManager.registerListener(this)
    }

    override fun onDisable() {

    }

    @Subscribe("fleetWarsFireBall-onRightClick")
    fun onInteract(event: PlayerInteractEvent) {
        if (event.item?.type == Material.FIREBALL && event.item?.itemMeta?.displayName == "FLEETWARS_FIREBALL") {
            event.isCancelled = true
            val fireballLocation = event.player.eyeLocation.add(event.player.location.direction.multiply(1))
            val fireBall = fireballLocation.world.spawnEntity(fireballLocation, EntityType.FIREBALL) as Fireball
            fireBall.velocity = event.player.location.direction.multiply(FleetWarsConfig.fireballSpeed)
            if (event.player.gameMode != GameMode.CREATIVE) {
                var amount = event.player.inventory.itemInHand.amount
                amount -= 1
                if (amount <= 0) {
                    event.player.inventory.itemInHand = null
                } else {
                    event.player.inventory.itemInHand?.amount = amount
                }
            }
        }
    }

    @Subscribe("fleetWarsFireBall-handleFireBallDespawn")
    fun handleFireBallDespawn(event: EntityTickEvent) {
        if (event.entity is Fireball) {
            if (event.entity.ticksLived > 200) {
                event.entity.remove()
            }
        }
    }
    @Subscribe("fleetWarsFireBall-disableExplosionDamage")
    fun disableExplosionDamage(event: ExplosionPrimeEvent) {
        if (event.entity is Fireball && sandbox) {
            event.isCancelled = true
            event.entity.remove()
            val location = event.entity.location
            event.entity.world.createExplosion(location.x, location.y, location.z, event.radius, false, false)
        }
    }

}