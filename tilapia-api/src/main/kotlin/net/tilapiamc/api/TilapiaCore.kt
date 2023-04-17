package net.tilapiamc.api

import net.tilapiamc.api.game.GameType
import net.tilapiamc.api.game.GamesManager
import net.tilapiamc.api.game.ManagedGame
import net.tilapiamc.api.internal.TilapiaInternal
import net.tilapiamc.common.language.LanguageManager
import net.tilapiamc.api.networking.GameFinder
import net.tilapiamc.api.server.TilapiaServer
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

interface TilapiaCore {

    companion object {
        lateinit var instance: TilapiaCore
    }

    val languageManager: LanguageManager
    val localGameManager: GamesManager
    val gameFinder: GameFinder

    fun provideGameId(gameType: GameType): UUID

    fun getLocalServer(): TilapiaServer

    fun addGame(game: ManagedGame)
    fun removeGame(game: ManagedGame)

    fun getInternal(): TilapiaInternal

    fun getPlugin(): JavaPlugin

    fun requireSchemaAccess(schema: String)


}