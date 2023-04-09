package net.tilapia.spigotcommon.game

import net.tilapia.api.events.AbstractEvent
import net.tilapia.api.events.EventsManager
import net.tilapia.api.events.FilteredEventListener
import net.tilapia.api.events.annotation.registerAnnotationBasedListener
import net.tilapia.api.events.annotation.unregisterAnnotationBasedListener
import net.tilapia.api.game.ManagedGame
import org.bukkit.event.Event
import org.bukkit.event.block.BlockEvent
import org.bukkit.event.entity.EntityEvent
import org.bukkit.event.hanging.HangingEvent
import org.bukkit.event.inventory.InventoryEvent
import org.bukkit.event.player.PlayerEvent
import org.bukkit.event.vehicle.VehicleEvent
import org.bukkit.event.weather.WeatherEvent

interface LocalGame: ManagedGame {
    val rules: ArrayList<AbstractRule>


    fun addRule(rule: AbstractRule) {
        synchronized(rules) {
            if (rule in rules) {
                logger.warn("Rule ${rule.name} is already added! Ignoring request...")
                return
            }
            logger.debug("Added rule \"${rule.name}\"")
            EventsManager.registerAnnotationBasedListener(rule, true) { shouldHandleEvent(it) }
            rules.add(rule)
        }
    }

    fun removeRule(rule: AbstractRule) {
        synchronized(rules) {
            if (rule !in rules) {
                logger.warn("Rule ${rule.name} is not yet added! Ignoring request..")
                return
            }
            logger.debug("Removed rule \"${rule.name}\"")
            EventsManager.unregisterAnnotationBasedListener(rule)
            rules.remove(rule)
        }
    }

    fun shouldHandleEvent(event: AbstractEvent): Boolean {
        if (event is EntityEvent) {
            return event.entity.world == gameWorld
        }
        if (event is PlayerEvent) {
            return event.player.world == gameWorld && event.player.uniqueId in players.map { it.uuid }
        }
        if (event is HangingEvent) {
            return event.entity.world == gameWorld
        }
        if (event is InventoryEvent) {
            return event.view.player.world == gameWorld
        }
        if (event is VehicleEvent) {
            return event.vehicle.world == gameWorld
        }
        if (event is WeatherEvent) {
            return event.world == gameWorld
        }
        return true
    }


}