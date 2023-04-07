package net.tilapia.api.player

import net.tilapia.api.TilapiaCore
import org.bukkit.entity.Player

abstract class LocalNetworkPlayer(core: TilapiaCore, val bukkitPlayer: Player):
    NetworkPlayer(core, bukkitPlayer.name, bukkitPlayer.uniqueId), Player by bukkitPlayer {



}