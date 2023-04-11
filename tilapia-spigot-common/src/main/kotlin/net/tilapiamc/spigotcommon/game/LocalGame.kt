package net.tilapiamc.spigotcommon.game

import net.tilapiamc.api.events.AbstractEvent
import net.tilapiamc.api.events.game.GameEvent
import net.tilapiamc.api.game.ManagedGame
import net.tilapiamc.spigotcommon.game.event.GameEventManager
import net.tilapiamc.spigotcommon.game.plugin.GamePlugin
import org.bukkit.event.block.BlockEvent
import org.bukkit.event.entity.EntityEvent
import org.bukkit.event.hanging.HangingEvent
import org.bukkit.event.inventory.InventoryEvent
import org.bukkit.event.player.PlayerEvent
import org.bukkit.event.vehicle.VehicleEvent
import org.bukkit.event.weather.WeatherEvent

interface LocalGame: ManagedGame {
    val rules: ArrayList<AbstractRule>

    val plugins: ArrayList<GamePlugin>
    val gameEventManager: GameEventManager

    fun applyPlugin(plugin: GamePlugin) {
        // TODO: Check if the plugin is in list
        plugins.add(plugin)
        plugin.game = this
        plugin.eventManager = gameEventManager
        plugin.onEnable()
    }


    fun removePlugin(plugin: GamePlugin) {
        // TODO: Check if the plugin is in list
        plugin.onDisable()
        plugins.remove(plugin)
    }

    fun endPlugins() {
        for (plugin in plugins) {
            plugin.onDisable()
        }
        plugins.clear()
    }

    fun addRule(rule: AbstractRule) {
        synchronized(rules) {
            if (rule in rules) {
                logger.warn("Rule ${rule.name} is already added! Ignoring request...")
                return
            }
            logger.debug("Added rule \"${rule.name}\"")
            gameEventManager.registerListener(rule, true)
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
            gameEventManager.unregisterListener(rule)
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
        if (event is BlockEvent) {
            return event.block.world == gameWorld
        }
        if (event is GameEvent) {
            return event.game == this
        }
        return true
    }


}