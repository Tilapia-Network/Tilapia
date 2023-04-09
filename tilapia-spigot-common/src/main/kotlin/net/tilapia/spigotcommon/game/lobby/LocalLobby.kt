package net.tilapia.spigotcommon.game.lobby

import net.tilapia.api.TilapiaCore
import net.tilapia.api.game.lobby.ManagedLobby
import net.tilapia.spigotcommon.game.AbstractRule
import net.tilapia.spigotcommon.game.LocalGame
import org.bukkit.World

abstract class LocalLobby(core: TilapiaCore, gameWorld: World): ManagedLobby(core, gameWorld), LocalGame {
    override val rules = ArrayList<AbstractRule>()

}