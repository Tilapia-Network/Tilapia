package net.tilapiamc.spigotcommon.game.lobby

import net.tilapiamc.api.TilapiaCore
import net.tilapia.api.game.lobby.ManagedLobby
import net.tilapiamc.spigotcommon.game.AbstractRule
import net.tilapiamc.spigotcommon.game.LocalGame
import org.bukkit.World

abstract class LocalLobby(core: net.tilapiamc.api.TilapiaCore, gameWorld: World): ManagedLobby(core, gameWorld), LocalGame {
    override val rules = ArrayList<AbstractRule>()

}