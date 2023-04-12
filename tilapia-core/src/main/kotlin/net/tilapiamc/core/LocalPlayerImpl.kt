package net.tilapiamc.core

import net.tilapiamc.api.player.LocalNetworkPlayer
import net.tilapiamc.language.LanguageCore
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.util.*

class LocalPlayerImpl(core: TilapiaCoreImpl, bukkitPlayer: Player): LocalNetworkPlayer(core, bukkitPlayer) {

    override val logger: Logger = LogManager.getLogger("PlayerLogger ${bukkitPlayer.name}")
    override val language: Locale = Locale.TRADITIONAL_CHINESE
    override val nameWithPrefix: String
        get() = "${ChatColor.BLUE}[開發者] $name"

    init {
        onJoin()
    }

    fun onJoin() {
        if (tilapiaCore.gamesManager.getAllLocalGames().isEmpty()) {
            logger.error("Could not find a game to join!")
            kickPlayer(getLanguageBundle()[LanguageCore.INVALID_JOIN_NO_GAME])
            return
        }
        sendToGame(findLobbyToJoin("main"))
    }

    fun onQuit() {

    }

}