package net.tilapiamc.fleetwars.customitems

import me.fan87.plugindevkit.utils.ItemStackBuilder
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object FleetWarsCustomItemProvider {

    fun createFireBall(): ItemStack {
        return ItemStackBuilder(Material.FIREBALL)
            .setDisplayName("FLEETWARS_FIREBALL")
            .build()
    }

}