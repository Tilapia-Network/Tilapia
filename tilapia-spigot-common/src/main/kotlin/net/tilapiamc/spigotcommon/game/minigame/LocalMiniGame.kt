package net.tilapiamc.spigotcommon.game.minigame

import net.tilapiamc.api.TilapiaCore
import net.tilapiamc.api.events.annotation.Subscribe
import net.tilapiamc.api.events.game.SpectatorJoinEvent
import net.tilapiamc.api.events.game.SpectatorQuitEvent
import net.tilapiamc.api.game.minigame.ManagedMiniGame
import net.tilapiamc.api.player.LocalNetworkPlayer
import net.tilapiamc.spigotcommon.game.AbstractRule
import net.tilapiamc.spigotcommon.game.LocalGame
import net.tilapiamc.spigotcommon.game.event.GameEventManager
import net.tilapiamc.spigotcommon.game.minigame.stage.MiniGameStage
import net.tilapiamc.spigotcommon.game.plugin.GamePlugin
import org.bukkit.World
import org.bukkit.entity.Player
import java.lang.NullPointerException

abstract class LocalMiniGame(core: TilapiaCore, gameWorld: World, lobbyType: String, miniGameType: String): ManagedMiniGame(core, gameWorld, lobbyType, miniGameType),
    LocalGame {
    override val rules = ArrayList<AbstractRule>()
    override val plugins = ArrayList<GamePlugin>()
    override val gameEventManager = GameEventManager(this)
    abstract val defaultStage: MiniGameStage
    val localPlayers: List<LocalNetworkPlayer>
        get() = super.players.filterIsInstance<LocalNetworkPlayer>()

    override fun end() {
        super.end()
        endPlugins()
    }

    var currentStage: MiniGameStage? = null
        set(value) {
            if (value == null) {
                throw NullPointerException("Stage could not be null")
            }
            if (field != null) {
                gameEventManager.unregisterListener(field!!)
                field!!.end()
            }
            gameEventManager.registerListener(value)
            value.start()
            field = value
        }

    init {
    }

    override fun start() {
        currentStage = defaultStage
        super.start()
    }

    @Subscribe("localMiniGame-onSpectatorJoin")
    fun onSpectatorJoin(event: SpectatorJoinEvent) {
        for (inGamePlayer in inGamePlayers) {
            inGamePlayer.hidePlayer(event.player)
        }
    }
    @Subscribe("localMiniGame-onSpectatorQuit")
    fun onSpectatorQuit(event: SpectatorQuitEvent) {
        for (inGamePlayer in inGamePlayers) {
            inGamePlayer.showPlayer(event.player)
        }
    }

    fun Player.isInGame(): Boolean = player.uniqueId in inGamePlayers.map { it.uniqueId }
    fun Player.isSpectator(): Boolean = player.uniqueId in spectatorPlayers.map { it.uniqueId }
}