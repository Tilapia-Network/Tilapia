package net.tilapiamc.sandbox

import net.tilapiamc.api.TilapiaCore
import net.tilapiamc.api.game.ManagedGame
import net.tilapiamc.api.game.getGameLanguageKey
import net.tilapiamc.api.player.LocalNetworkPlayer
import net.tilapiamc.api.player.NetworkPlayer
import net.tilapiamc.spigotcommon.game.lobby.LocalLobby
import net.tilapiamc.spigotcommon.utils.TemporaryWorldProvider
import org.bukkit.ChatColor
import org.bukkit.World

class TilapiaSandbox(core: TilapiaCore, world: World): LocalLobby(core, TemporaryWorldProvider.createTemporaryWorldFromWorld(world), "main") {

    init {

    }

    override fun onStart() {

    }

    override fun onEnd() {

    }



    val forceJoinOnly = getGameLanguageKey("FORCE_JOIN_ONLY", "沙盒世界必須使用強制加入")
    override fun couldAddPlayer(networkPlayer: NetworkPlayer, forceJoin: Boolean): ManagedGame.PlayerJoinResult {
        return if (forceJoin)
            ManagedGame.PlayerJoinResult(ManagedGame.PlayerJoinResultType.ACCEPTED, 1.0)
        else
            ManagedGame.PlayerJoinResult(ManagedGame.PlayerJoinResultType.DENIED, 0.0, networkPlayer.getLanguageBundle()[forceJoinOnly])
    }

    val joinMessage = getGameLanguageKey("JOIN_MESSAGE", "${ChatColor.GREEN}[+] %1\$s")
    val quitMessage = getGameLanguageKey("QUIT_MESSAGE", "${ChatColor.RED}[-] %1\$s")
    override fun addPlayer(networkPlayer: LocalNetworkPlayer) {
        localPlayers.forEach {
            it.sendMessage(it.getLanguageBundle()[joinMessage].format(it.nameWithPrefix))
        }
    }

    override fun removePlayer(networkPlayer: LocalNetworkPlayer) {
        localPlayers.forEach {
            it.sendMessage(it.getLanguageBundle()[quitMessage].format(it.nameWithPrefix))
        }
    }


}