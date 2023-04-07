package net.tilapia.core

import net.tilapia.api.TilapiaCore
import net.tilapia.api.player.LocalNetworkPlayer
import org.bukkit.entity.Player

class LocalPlayerImpl(core: TilapiaCoreImpl, bukkitPlayer: Player): LocalNetworkPlayer(core, bukkitPlayer) {



}