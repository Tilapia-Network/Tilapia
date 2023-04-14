package net.tilapiamc.gameextension.plugins

import net.tilapiamc.api.events.annotation.Subscribe
import net.tilapiamc.spigotcommon.game.plugin.GamePlugin
import org.bukkit.entity.Boat
import org.bukkit.event.vehicle.VehicleDestroyEvent

class PluginNoBoatCrash: GamePlugin() {

    companion object {


    }

    override fun onEnable() {
        eventManager.registerListener(this)
    }

    override fun onDisable() {

    }


    @Subscribe("noBoatCrash-onVehicleDestroy")
    fun onVehicleDestroy(event: VehicleDestroyEvent) {
        if (event.vehicle is Boat && event.attacker == null) {
            event.isCancelled = true
        }
    }

}

