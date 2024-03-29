package net.tilapiamc.spigotcommon.game.minigame.stage

import net.tilapiamc.spigotcommon.game.AbstractRule
import net.tilapiamc.spigotcommon.game.minigame.LocalMiniGame
import net.tilapiamc.spigotcommon.game.plugin.GamePlugin
import org.bukkit.entity.Player

abstract class MiniGameStage(
    open val miniGame: LocalMiniGame,
    val name: String,
) {

    private val plugins = ArrayList<GamePlugin>()
    private val rules = ArrayList<AbstractRule>()

    fun applyPlugin(plugin: GamePlugin) {
        miniGame.applyPlugin(plugin)
        plugins.add(plugin)
    }


    fun removePlugin(plugin: GamePlugin) {
        miniGame.removePlugin(plugin)
        plugins.remove(plugin)
    }

    fun endPlugins() {
        for (plugin in plugins) {
            miniGame.removePlugin(plugin)
        }
        plugins.clear()
    }

    fun addRule(rule: AbstractRule) {
        miniGame.addRule(rule)
        rules.add(rule)
    }

    fun removeRule(rule: AbstractRule) {
        miniGame.removeRule(rule)
        rules.remove(rule)
    }

    fun start() {
        onStart()
    }
    abstract fun onStart()

    fun end() {
        endPlugins()
        for (rule in ArrayList(rules)) {
            removeRule(rule)
        }
        onEnd()
    }
    abstract fun onEnd()
    fun Player.isInGame(): Boolean = player.uniqueId in miniGame.inGamePlayers.map { it.uniqueId }
    fun Player.isSpectator(): Boolean = player.uniqueId in miniGame.spectatorPlayers.map { it.uniqueId }
}