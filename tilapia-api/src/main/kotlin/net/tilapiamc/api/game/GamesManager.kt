package net.tilapiamc.api.game

import net.tilapiamc.api.TilapiaCore
import net.tilapiamc.api.events.EventsManager
import net.tilapiamc.api.events.annotation.Subscribe
import net.tilapiamc.api.events.annotation.registerAnnotationBasedListener
import net.tilapiamc.api.game.lobby.Lobby
import net.tilapiamc.api.game.minigame.MiniGame
import net.tilapiamc.api.language.LanguageKey
import net.tilapiamc.api.player.PlayersManager.getLocalPlayer
import org.bukkit.event.player.PlayerAchievementAwardedEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRespawnEvent
import java.util.UUID

class GamesManager {

    private val games = HashMap<UUID, ManagedGame>()

    init {
        EventsManager.registerAnnotationBasedListener(this)
    }

    fun registerManagedGame(game: ManagedGame) {
        games[game.gameId] = game
    }
    fun unregisterManagedGame(uuid: UUID) {
        games.remove(uuid)
    }

    fun getAllLocalGames(): Set<ManagedGame> = games.values.toSet()
    fun getLocalGameById(uuid: UUID): ManagedGame? = games[uuid]

    @Subscribe("GamesManager-onPlayerQuit", mustRunBefore = ["playerLeaveInit"])
    fun onPlayerQuit(event: PlayerQuitEvent) {
        event.quitMessage = null
        for (game in games.values.filter { event.player.uniqueId in it.players.map { it.uuid } }) {
            if (game is ManagedGame) {
                game.remove(event.player.getLocalPlayer())
            }
        }
    }
    @Subscribe("GamesManager-onPlayerRespawn")
    fun onPlayerRespawn(event: PlayerRespawnEvent) {
        event.respawnLocation = event.player.world.spawnLocation
    }
    @Subscribe("GamesManager-onPlayerJoin")
    fun onPlayerJoin(event: PlayerJoinEvent) {
        event.joinMessage = null
    }
    @Subscribe("GamesManager-onAchievement")
    fun onAchievement(event: PlayerAchievementAwardedEvent) {
        event.isCancelled = true
    }


}

fun ManagedGame.getGameLanguageKey(name: String, defaultValue: String): LanguageKey {
    val key = LanguageKey("GAME_${gameType}_${if (this is Lobby) this.lobbyType else (this as MiniGame).miniGameType}_${name.replace("-", "_").uppercase()}_$name", defaultValue)
    TilapiaCore.instance.languageManager.registerLanguageKey(key)
    return key
}