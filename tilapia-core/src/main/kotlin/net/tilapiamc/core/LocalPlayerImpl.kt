package net.tilapiamc.core

import net.tilapiamc.api.TilapiaCore
import net.tilapia.api.game.GamesManager
import net.tilapia.api.player.LocalNetworkPlayer
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bukkit.ChatColor
import org.bukkit.entity.Player

class LocalPlayerImpl(core: TilapiaCoreImpl, bukkitPlayer: Player): LocalNetworkPlayer(core, bukkitPlayer) {

    override val logger: Logger = LogManager.getLogger("PlayerLogger ${bukkitPlayer.name}")

    init {
        onJoin()
    }

    fun onJoin() {
        if (GamesManager.getAllGames().isEmpty()) {
            logger.error("Could not find a game to join!")
            kickPlayer("${ChatColor.RED}Could not find a game for you to join at the moment!")
            return
        }
        sendToGame(GamesManager.getAllGames().first())
    }

    fun onQuit() {

    }

}