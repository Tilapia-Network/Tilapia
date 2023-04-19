package net.tilapiamc.core

import kotlinx.coroutines.runBlocking
import net.tilapiamc.api.game.Game
import net.tilapiamc.api.player.LocalNetworkPlayer
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.util.*

class LocalPlayerImpl(core: TilapiaCoreImpl, bukkitPlayer: Player,
): LocalNetworkPlayer(core, bukkitPlayer) {

    override val logger: Logger = LogManager.getLogger("PlayerLogger ${bukkitPlayer.name}")
    override val language: Locale = Locale.TRADITIONAL_CHINESE
    override fun where(): Game {
        return tilapiaCore.localGameManager.getAllLocalGames().first { this in it.players } as Game
    }

    override fun send(game: Game, forceJoin: Boolean, spectate: Boolean) {
        return runBlocking {
            tilapiaCore.getInternal().sendToGame(this@LocalPlayerImpl, game, forceJoin, spectate)
        }
    }

    override val nameWithPrefix: String
        get() = "$prefix $name"
    override val prefix: String
        get() = "$prefixColor[開發者] "
    override val prefixColor: String
        get() = "${ChatColor.BLUE}"

    init {
        onJoin()
    }

    fun onJoin() {
    }

    fun onQuit() {

    }

    override fun equals(other: Any?): Boolean {
        return super.equals(other) || (other is Player && other == bukkitPlayer)
    }
}