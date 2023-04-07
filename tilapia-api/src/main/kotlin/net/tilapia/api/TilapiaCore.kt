package net.tilapia.api

import net.tilapia.api.game.Game
import net.tilapia.api.game.GameType
import net.tilapia.api.game.minigame.ManagedMiniGame
import net.tilapia.api.server.TilapiaServer
import java.lang.reflect.Proxy
import java.util.UUID

interface TilapiaCore {

    companion object {
        lateinit var instance: TilapiaCore
    }

    fun provideGameId(gameType: GameType): UUID

    fun getLocalServer(): TilapiaServer

    fun addGame(game: Game)


}