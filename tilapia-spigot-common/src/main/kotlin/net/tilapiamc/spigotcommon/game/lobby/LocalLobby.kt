package net.tilapiamc.spigotcommon.game.lobby

import net.tilapiamc.api.TilapiaCore
import net.tilapiamc.api.game.lobby.ManagedLobby
import net.tilapiamc.spigotcommon.game.AbstractRule
import net.tilapiamc.spigotcommon.game.LocalGame
import net.tilapiamc.spigotcommon.game.event.GameEventManager
import net.tilapiamc.spigotcommon.game.plugin.GamePlugin
import org.bukkit.World

abstract class LocalLobby(core: TilapiaCore, gameWorld: World): ManagedLobby(core, gameWorld), LocalGame {
    override val rules = ArrayList<AbstractRule>()

    override val plugins = ArrayList<GamePlugin>()
    override val gameEventManager = GameEventManager(this)

    override fun end() {
        super.end()
        endPlugins()
    }
}