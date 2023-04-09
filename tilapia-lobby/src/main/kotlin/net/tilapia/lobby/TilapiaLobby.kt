package net.tilapia.lobby

import net.tilapia.api.TilapiaCore
import net.tilapia.api.game.ManagedGame
import net.tilapia.api.player.LocalNetworkPlayer
import net.tilapia.api.player.NetworkPlayer
import net.tilapia.gameextension.rules.impl.RuleNoDestruction
import net.tilapia.spigotcommon.game.lobby.LocalLobby
import org.bukkit.World

class TilapiaLobby(core: TilapiaCore, world: World): LocalLobby(core, world) {

    init {
        addRule(RuleNoDestruction(this,
            protectEntities = true,
            protectUseEntities = true,
            protectPlayers = true,
            protectUsePlayers = true,
            protectBlockPlacement = true,
            protectBlockUse = false,
            protectItemUse = true,
            protectBlockBreak = true
        ))
    }

    override fun couldAddPlayer(networkPlayer: NetworkPlayer): Double {
        return 1.0
    }

    override fun preAddPlayer(networkPlayer: NetworkPlayer): ManagedGame.PlayerJoinResult {
        return ManagedGame.PlayerJoinResult(ManagedGame.PlayerJoinResultType.ACCEPTED)
    }

    override fun addPlayer(networkPlayer: LocalNetworkPlayer) {

    }

    override fun removePlayer(networkPlayer: LocalNetworkPlayer) {

    }


}