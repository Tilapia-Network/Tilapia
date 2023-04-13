package net.tilapiamc.api.player

import net.tilapiamc.api.TilapiaCore
import org.apache.logging.log4j.Logger
import org.bukkit.GameMode
import org.bukkit.entity.Player

abstract class LocalNetworkPlayer(core: TilapiaCore, val bukkitPlayer: Player):
    NetworkPlayer(core, bukkitPlayer.name, bukkitPlayer.uniqueId), Player by bukkitPlayer {
    override val isLocal: Boolean = true
    abstract val logger: Logger

    abstract val nameWithPrefix: String
    abstract val prefix: String
    abstract val prefixColor: String


    companion object {
        fun Player.resetBukkitPlayer() {
            closeInventory()
            inventory.heldItemSlot = 0
            inventory.clear()
            allowFlight = false
            isFlying = false
            compassTarget = world.spawnLocation
            gameMode = GameMode.SURVIVAL
            health = 20.0
            saturation = 1f
            foodLevel = 20
            exp = 0f
            level = 0
            bedSpawnLocation = null
            activePotionEffects.forEach { removePotionEffect(it.type) }
            leaveVehicle()
        }
    }

    fun resetPlayerState() {
        // TODO: Temp solution. Gonna kick the player and re-connect
        // Gotta find a way to disable player data save
        resetBukkitPlayer()
    }



}