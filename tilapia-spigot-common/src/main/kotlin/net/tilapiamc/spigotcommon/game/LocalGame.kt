package net.tilapiamc.spigotcommon.game

import com.comphenix.protocol.injector.server.TemporaryPlayer
import net.citizensnpcs.api.event.NPCEvent
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
import org.bukkit.event.world.WorldEvent

interface LocalGame: ManagedGame {
    val rules: ArrayList<AbstractRule>

    val plugins: ArrayList<GamePlugin>
    val gameEventManager: GameEventManager

    fun applyPlugin(plugin: GamePlugin) {
        if (plugin in plugins) {
            logger.warn("Plugin is already in the applied! Ignoring", Exception())
            return
        }
        plugins.add(plugin)
        plugin.game = this
        plugin.eventManager = GameEventManager(this)
        plugin.onEnable()
    }


    fun removePlugin(plugin: GamePlugin) {
        if (plugin !in plugins) {
            logger.warn("Plugin is already disabled! Ignoring", Exception())
            return
        }
        plugin.onDisable()
        plugin.eventManager.unregisterAll()
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
            if (event.player is TemporaryPlayer) {
                return false
            }
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
        if (event is WorldEvent) {
            return event.world == gameWorld
        }
        if (event is NPCEvent) {
            return event.npc.entity.world == gameWorld
        }
        return true
    }


}