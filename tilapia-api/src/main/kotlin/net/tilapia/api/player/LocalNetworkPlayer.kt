package net.tilapia.api.player

import com.avaje.ebean.bean.NodeUsageListener
import net.tilapia.api.TilapiaCore
import org.bukkit.GameMode
import org.bukkit.entity.Player

abstract class LocalNetworkPlayer(core: TilapiaCore, val bukkitPlayer: Player):
    NetworkPlayer(core, bukkitPlayer.name, bukkitPlayer.uniqueId), Player by bukkitPlayer {


    fun resetPlayerState() {
        closeInventory()
        inventory.heldItemSlot = 0
        inventory.clear()
        gameMode = GameMode.SURVIVAL
        health = 20.0
        saturation = 1f
        foodLevel = 20
        bedSpawnLocation = null
    }

}