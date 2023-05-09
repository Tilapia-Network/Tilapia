package net.tilapiamc.fleetwars.customitems

import me.fan87.plugindevkit.events.EntityTickEvent
import net.tilapiamc.api.player.LocalNetworkPlayer
import net.tilapiamc.customib.NamespacedKey
import net.tilapiamc.customib.item.CustomItem
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import net.minecraft.server.v1_8_R3.NBTTagCompound
import net.tilapiamc.common.events.annotation.Subscribe
import net.tilapiamc.fleetwars.config.FleetWarsConfig
import org.bukkit.GameMode
import org.bukkit.entity.EntityType
import org.bukkit.entity.Fireball
import org.bukkit.event.entity.ExplosionPrimeEvent
import org.bukkit.event.player.PlayerInteractEvent

class ItemFireBall(val sandbox: Boolean): CustomItem(NamespacedKey("fleetwars", "fireball"), "${ChatColor.GREEN}火焰彈") {

    override val material: Material = Material.FIREBALL
    override val damage: Short = 0

    override fun applyDisplays(player: LocalNetworkPlayer, itemStack: ItemStack): ItemStack {
        return itemStack
    }

    override fun applyExtraNBT(nbt: NBTTagCompound) {

    }

    @Subscribe("fleetWarsFireBall-onRightClick")
    fun onInteract(event: PlayerInteractEvent) {
        if (isAnItemOf(event.item)) {
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