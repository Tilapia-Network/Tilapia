package net.tilapia.api.player

import org.bukkit.entity.Player

abstract class LocalNetworkPlayer: NetworkPlayer() {

    abstract val bukkitPlayer: Player

}