package net.tilapia.api.player

import org.bukkit.entity.Player

abstract class LocalNetworkPlayer(val bukkitPlayer: Player): NetworkPlayer(), Player by bukkitPlayer {



}