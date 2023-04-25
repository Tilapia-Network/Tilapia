package net.tilapiamc.spigotcommon.game.minigame

import net.tilapiamc.api.TilapiaCore
import net.tilapiamc.common.events.annotation.Subscribe
import net.tilapiamc.api.events.game.SpectatorJoinEvent
import net.tilapiamc.api.events.game.SpectatorQuitEvent
import net.tilapiamc.api.game.minigame.ManagedMiniGame
import net.tilapiamc.api.player.LocalNetworkPlayer
import net.tilapiamc.spigotcommon.game.AbstractRule
import net.tilapiamc.spigotcommon.game.LocalGame
import net.tilapiamc.spigotcommon.game.event.GameEventManager
import net.tilapiamc.spigotcommon.game.minigame.stage.MiniGameStage
import net.tilapiamc.spigotcommon.game.plugin.GamePlugin
import org.bukkit.GameMode
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.*
import org.bukkit.event.inventory.InventoryInteractEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerPickupItemEvent
import org.bukkit.event.vehicle.VehicleDamageEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

abstract class LocalMiniGame(core: TilapiaCore, gameWorld: World, lobbyType: String, miniGameType: String): ManagedMiniGame(core, gameWorld, lobbyType, miniGameType),
    LocalGame {
    override val rules = ArrayList<AbstractRule>()
    override val plugins = ArrayList<GamePlugin>()
    override val gameEventManager = GameEventManager(this)
    abstract val defaultStage: MiniGameStage
    val localPlayers: List<LocalNetworkPlayer>
        get() = super.players.filterIsInstance<LocalNetworkPlayer>()

    override fun end() {
        super.end()
        endPlugins()
    }

    var currentStage: MiniGameStage? = null
        set(value) {
            if (value == null) {
                throw NullPointerException("Stage could not be null")
            }
            if (field != null) {
                gameEventManager.unregisterListener(field!!)
                field!!.end()
            }
            gameEventManager.registerListener(value)
            value.start()
            field = value
        }

    init {
    }

    override fun start() {
        currentStage = defaultStage
        applyPlugin(PluginSpectator(this) { isInGame() || gameMode == GameMode.CREATIVE })
        super.start()
    }



    fun Player.isInGame(): Boolean = player.uniqueId in inGamePlayers.map { it.uniqueId }
    fun Player.isSpectator(): Boolean = player.uniqueId in spectatorPlayers.map { it.uniqueId }
}


class PluginSpectator(
    val miniGame: LocalMiniGame, val ignorePlayer: Player.() -> Boolean = { gameMode == GameMode.CREATIVE }
): GamePlugin() {

    @Subscribe("spectator-onSpectatorJoin")
    fun onSpectatorJoin(event: SpectatorJoinEvent) {
        for (inGamePlayer in miniGame.inGamePlayers) {
            inGamePlayer.bukkitPlayer.hidePlayer(event.player)
        }
        event.player.allowFlight = true
        event.player.isFlying = true
        event.player.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, Int.MAX_VALUE - 2, 1, true, false))
    }
    @Subscribe("spectator-onSpectatorQuit")
    fun onSpectatorQuit(event: SpectatorQuitEvent) {
        for (inGamePlayer in miniGame.inGamePlayers) {
            inGamePlayer.bukkitPlayer.showPlayer(event.player)
        }
    }

    @Subscribe("NoDestruction-onEntityDamage")
    fun onEntityDamage(event: EntityDamageEvent) {
        if (event is EntityDamageByEntityEvent) {
            if (event.damager is Player) {
                if (!ignorePlayer(event.damager as Player)) {
                    event.isCancelled = true
                }
            }
        }
        if (event.entity is Player) {
            if (ignorePlayer(event.entity as Player)) {
                return
            }
            event.isCancelled = true
            return
        }
        event.isCancelled = true
    }
    @Subscribe("NoDestruction-onVehicleDamage")
    fun onVehicleDamage(event: VehicleDamageEvent) {
        if (event.attacker is Player && ignorePlayer(event.attacker as Player)) {
            return
        }
        event.isCancelled = true

    }

    @Subscribe("NoDestruction-onHungerChange")
    fun onHungerChange(event: FoodLevelChangeEvent) {
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

    @Subscribe("NoDestruction-onEntityUse")
    fun onEntityUse(event: PlayerInteractEntityEvent) {
        if (ignorePlayer(event.player)) {
            return
        }
        if (event.rightClicked is Player) {
            event.isCancelled = true
            return
        }
        event.isCancelled = true

    }
    @Subscribe("NoDestruction-onBlockDestroy")
    fun onBlockDestroy(event: BlockBreakEvent) {
        if (ignorePlayer(event.player)) {
            return
        }
        event.isCancelled = true

    }
    @Subscribe("NoDestruction-onBlockPlace")
    fun onBlockPlace(event: BlockPlaceEvent) {
        if (ignorePlayer(event.player)) {
            return
            event.isCancelled = true
        }
    }
    @Subscribe("NoDestruction-onItemUse")
    fun onItemUse(event: PlayerInteractEvent) {
        if (ignorePlayer(event.player)) {
            return
        }
        if (event.hasItem()) {
            event.isCancelled = true

        }
    }
    @Subscribe("NoDestruction-onPlayerPhysical")
    fun onPlayerPhysical(event: PlayerInteractEvent) {
        if (ignorePlayer(event.player)) {
            return
        }
        if (event.action == Action.PHYSICAL) {
            event.isCancelled = true

        }
    }
    @Subscribe("NoDestruction-onBlockUse")
    fun onBlockUse(event: PlayerInteractEvent) {
        if (ignorePlayer(event.player)) {
            return
        }
        if (event.hasBlock()) {
            event.isCancelled = true

        }
    }
    @Subscribe("NoDestruction-onItemDrop")
    fun onItemDrop(event: PlayerDropItemEvent) {
        if (ignorePlayer(event.player)) {
            return
        }
        event.isCancelled = true

    }
    @Subscribe("NoDestruction-onItemPickup")
    fun onItemPickup(event: PlayerPickupItemEvent) {
        if (ignorePlayer(event.player)) {
            return
        }
        event.isCancelled = true

    }
    @Subscribe("NoDestruction-onPlayerInventoryChange")
    fun onPlayerInventoryChange(event: InventoryInteractEvent) {
        if (ignorePlayer(event.whoClicked as Player)) {
            return
        }
        if (event.whoClicked == null) {
            return
        }
        event.isCancelled = true

    }

    override fun onEnable() {
        eventManager.registerListener(this)
    }

    override fun onDisable() {

    }

}