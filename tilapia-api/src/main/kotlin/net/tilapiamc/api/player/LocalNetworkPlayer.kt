package net.tilapiamc.api.player

import net.tilapiamc.api.TilapiaCore
import net.tilapiamc.api.game.Game
import org.apache.logging.log4j.Logger
import org.bukkit.GameMode
import org.bukkit.entity.Player
import java.util.*

abstract class LocalNetworkPlayer(core: TilapiaCore, val bukkitPlayer: Player):
    NetworkPlayer(core, bukkitPlayer.name, Locale.TRADITIONAL_CHINESE, bukkitPlayer.uniqueId), Player by bukkitPlayer {
    // TODO: Load locale from database

    var currentGameId: UUID? = null
    val currentGame: Game?
        get() = if (currentGameId == null) null else tilapiaCore.localGameManager.getLocalGameById(currentGameId!!) as Game
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