package net.tilapiamc.api

import net.tilapiamc.api.game.Game
import net.tilapiamc.api.game.GameType
import net.tilapiamc.api.game.minigame.ManagedMiniGame
import net.tilapiamc.api.internal.TilapiaInternal
import net.tilapiamc.api.player.NetworkPlayer
import net.tilapiamc.api.server.TilapiaServer
import java.lang.reflect.Proxy
import java.util.UUID

interface TilapiaCore {

    companion object {
        lateinit var instance: net.tilapiamc.api.TilapiaCore
    }

    fun provideGameId(gameType: GameType): UUID

    fun getLocalServer(): TilapiaServer

    fun addGame(game: Game)
    fun removeGame(game: Game)

    fun getInternal(): TilapiaInternal


}